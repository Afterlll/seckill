package cn.wolfcode.service.impl;

import cn.wolfcode.common.exception.BusinessException;
import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.AccountTransaction;
import cn.wolfcode.domain.OperateIntergralVo;
import cn.wolfcode.mapper.AccountTransactionMapper;
import cn.wolfcode.mapper.UsableIntegralMapper;
import cn.wolfcode.service.IUsableIntegralService;
import cn.wolfcode.web.msg.IntergralCodeMsg;
import com.alibaba.fastjson.JSON;
import io.seata.rm.tcc.api.BusinessActionContext;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by lanxw
 */
@Service
public class UsableIntegralServiceImpl implements IUsableIntegralService {
    @Autowired
    private UsableIntegralMapper usableIntegralMapper;
    @Autowired
    private AccountTransactionMapper accountTransactionMapper;

    @Override
    public void decrIntergral(OperateIntergralVo vo) {
        int effectCount = usableIntegralMapper.decrIntergral(vo.getUserId(), vo.getValue());
        if (effectCount == 0) { // 积分不足
            throw new BusinessException(IntergralCodeMsg.INTERGRAL_NOT_ENOUGH);
        }
    }

    // 插入一条记录
    @Override
    public int insert(BusinessActionContext context, OperateIntergralVo vo, int status) {
        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setTxId(context.getXid()); // 全局事务id
        accountTransaction.setActionId(context.getBranchId()); // 事务分支id
        accountTransaction.setUserId(vo.getUserId()); // 用户id
        accountTransaction.setAmount(vo.getValue()); // 扣减积分的数量
        Date date = new Date();
        accountTransaction.setGmtCreated(date); // 当前记录的创建时间
        accountTransaction.setGmtModified(date); // 当前记录的修改时间
        accountTransaction.setState(status); // 当前事务的状态（try，commit，rollback）
        return accountTransactionMapper.insert(accountTransaction);
    }

    // 扣减积分try方法
    @Override
    @Transactional // 保证原子性
    public void decrIntergralTry(OperateIntergralVo vo, BusinessActionContext context) {
        System.out.println("执行try方法");
        // 1. 插入事务控制记录
//        int effectCount = accountTransactionMapper.insert(accountTransaction);
        int effectCount = insert(context, vo, AccountTransaction.STATE_TRY);
        // 2. 判断是否插入成功（插入失败就不能继续执行业务逻辑了）
        if (effectCount != 0) {
            // 3. 执行扣减积分业务逻辑(使用冻结金额的方式)
            usableIntegralMapper.freezeIntergral(vo.getUserId(), vo.getValue());
        } else {
//            throw new BusinessException(IntergralCodeMsg.INTERGRAL_NOT_ENOUGH);
        }
        int i = 1 / 0;
    }
    // 扣减积分commit方法
    @Override
    public void decrIntergralCommit(BusinessActionContext context) {
        System.out.println("执行commit方法");
        // 1. 查询事务控制记录
        AccountTransaction accountTransaction = accountTransactionMapper.get(context.getXid(), context.getBranchId());
        // 2. 是否存在记录
        if (accountTransaction != null) {
            // 2.1 存在记录
            if (accountTransaction.getState() == AccountTransaction.STATE_TRY) {
                // 2.1.1 状态是从try过来的，继续执行commit（是初始状态）
                // 执行commit逻辑
                // 将记录修改为commit
                int effectCount = accountTransactionMapper.updateAccountTransactionState(context.getXid(), context.getBranchId(), AccountTransaction.STATE_COMMIT, AccountTransaction.STATE_TRY);
                // 真正的扣减积分
                usableIntegralMapper.decrIntergral(accountTransaction.getUserId(), accountTransaction.getAmount());
                if (effectCount != 2) {
                    // 通知管理员
//                    throw new BusinessException(IntergralCodeMsg.OP_INTERGRAL_ERROR);
                }
            } else if (accountTransaction.getState() == AccountTransaction.STATE_COMMIT) {
                // 2.1.1 不是初始状态（运行幂等操作）
                // 如果为commit，不做如何操作
            } else {
                // 其他状态，mq通知管理员
            }
        } else {
            // 2.2 不存在记录直接抛出异常接收二阶段 TODO （或者通过mq通知管理员）
            // 如果抛出异常commit方法会不断的重试
//            throw new BusinessException(IntergralCodeMsg.OP_INTERGRAL_ERROR);
        }
    }
    // 扣减积分rollback方法
    @Override
    @Transactional
    public void decrIntergralRollback(BusinessActionContext context) {
        System.out.println("执行rollback方法");
        // 1. 查询事务控制记录
        AccountTransaction accountTransaction = accountTransactionMapper.get(context.getXid(), context.getBranchId());
        if (accountTransaction != null) {
            // 2. 存在日志记录
            if (accountTransaction.getState() == AccountTransaction.STATE_TRY) {
                // 2. 1从try过来的，正常流程
                // 修改日志记录
                int i = accountTransactionMapper.updateAccountTransactionState(context.getXid(), context.getBranchId(), AccountTransaction.STATE_CANCEL, AccountTransaction.STATE_TRY);
                // 执行rollback逻辑，增加积分
                i += usableIntegralMapper.incrIntergral(accountTransaction.getUserId(), accountTransaction.getAmount());
                if (i != 2) {
                    throw new BusinessException(IntergralCodeMsg.OP_INTERGRAL_ERROR);
                }
            } else if (accountTransaction.getState() == AccountTransaction.STATE_CANCEL) {
                // 之前已经执行过cancle，幂等处理
            } else {
                // 其他状态，mq通知管理员
            }
        } else {
            // 不存在日志记录
            // 需要插入当前日志情况，防止空回滚
            String s = JSON.toJSONString(context.getActionContext("vo"));
            OperateIntergralVo vo = JSON.parseObject(s, OperateIntergralVo.class);
            int effectCount = insert(context, vo, AccountTransaction.STATE_CANCEL);
//            int i = insert(context, vo, AccountTransaction.STATE_CANCEL);
            if (effectCount == 0) {
                // 插入失败（通知管理员）
            }
        }
    }

    @Override
    public void incrIntergral(OperateIntergralVo vo) {
        int effectCount = usableIntegralMapper.incrIntergral(vo.getUserId(), vo.getValue());
        if (effectCount == 0) { // 积分不足
            throw new BusinessException(IntergralCodeMsg.INTERGRAL_NOT_ENOUGH);
        }
    }

    // 增加积分try方法
    @Override
    public void incrIntergralTry(OperateIntergralVo vo, BusinessActionContext context) {
        // 添加日志记录
        int insert = insert(context, vo, AccountTransaction.STATE_TRY);
        // 增加不需要执行业务操作，是空操作
        if (insert == 0) {
            // 通知管理员
        }
    }
    // 增加积分commit方法
    @Override
    @Transactional
    public void incrIntergralCommit(BusinessActionContext context) {
        // 查询日志记录
        AccountTransaction accountTransaction = accountTransactionMapper.get(context.getXid(), context.getBranchId());
        if (accountTransaction != null) {
            if (accountTransaction.getState() == AccountTransaction.STATE_TRY) {
                // 修改状态
                int i = accountTransactionMapper.updateAccountTransactionState(context.getXid(), context.getBranchId(), AccountTransaction.STATE_COMMIT, AccountTransaction.STATE_TRY);
                // 真正的添加积分
                i += usableIntegralMapper.incrIntergral(accountTransaction.getUserId(), accountTransaction.getAmount());
                if (i != 2) {

                }
            } else if (accountTransaction.getState() == AccountTransaction.STATE_COMMIT) {

            } else {

            }
        } else {

        }
    }
    // 增加积分rollback方法
    @Override
    public void incrIntergralRollback(BusinessActionContext context) {
        AccountTransaction accountTransaction = accountTransactionMapper.get(context.getXid(), context.getBranchId());
        if (accountTransaction != null) {
            if (accountTransaction.getState() == AccountTransaction.STATE_TRY) {
                // 修改日志记录
                accountTransactionMapper.updateAccountTransactionState(context.getXid(), context.getBranchId(), AccountTransaction.STATE_CANCEL, AccountTransaction.STATE_TRY);
            }
        } else {
            // 没有记录需要插入一条日志记录表示该事务已经是取消状态了，防止空回滚
            OperateIntergralVo vo = JSON.parseObject(JSON.toJSONString(context.getActionContext("vo")), OperateIntergralVo.class);
            insert(context, vo, AccountTransaction.STATE_CANCEL);
        }
    }
}
