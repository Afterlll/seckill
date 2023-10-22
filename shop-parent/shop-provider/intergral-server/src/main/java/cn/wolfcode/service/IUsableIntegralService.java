package cn.wolfcode.service;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.OperateIntergralVo;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import org.apache.ibatis.annotations.Param;

/**
 * Created by lanxw
 */
@LocalTCC // TCC事务
public interface IUsableIntegralService {
    /**
     * 扣减积分
     * @param vo
     */
    void decrIntergral(OperateIntergralVo vo);

    int insert(BusinessActionContext context, OperateIntergralVo vo, int status);

    /**
     * 扣减积分Try方法
     * @param vo 远程秒杀服务传递过来的数据
     * @param context TCC上下文对象，会自动注入全局事务id、分支id、....
     */
    @TwoPhaseBusinessAction(name = "decrIntergralTry", commitMethod = "decrIntergralCommit", rollbackMethod = "decrIntergralRollback")
    void decrIntergralTry(@BusinessActionContextParameter(paramName = "vo") OperateIntergralVo vo, BusinessActionContext context);

    /**
     * 扣减积分Commit方法
     * @param context TCC上下文对象
     */
    void decrIntergralCommit(BusinessActionContext context);

    /**
     * 扣减积分Rollback方法
     * @param context TCC上下文对象
     */
    void decrIntergralRollback(BusinessActionContext context);


    void incrIntergral(OperateIntergralVo vo);

    /**
     * 增加积分try方法
     * @param vo
     * @param context
     */
    @TwoPhaseBusinessAction(name = "incrIntergralTry", rollbackMethod = "incrIntergralRollback", commitMethod = "incrIntergralCommit")
    void incrIntergralTry(@BusinessActionContextParameter(paramName = "vo") OperateIntergralVo vo, BusinessActionContext context);

    /**
     * 增加积分commit方法
     * @param context
     */
    void incrIntergralCommit(BusinessActionContext context);

    /**
     * 增加积分rollback方法
     * @param context
     */
    void incrIntergralRollback(BusinessActionContext context);
}
