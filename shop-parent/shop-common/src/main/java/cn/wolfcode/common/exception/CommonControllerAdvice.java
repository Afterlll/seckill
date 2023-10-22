package cn.wolfcode.common.exception;

import cn.wolfcode.common.web.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Created by wolfcode-lanxw
 */
public class CommonControllerAdvice {
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Result handleBusinessException(BusinessException ex){
        return Result.error(ex.getCodeMsg());
    }
//    @ExceptionHandler(NullPointerException.class)
//    public void handleNullPointerException(NullPointerException ex) {
//        ex.printStackTrace();//在控制台打印错误消息.
//    }
//    // 接收数据库中主键冲突异常
//    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
//    public void handleNullPointerException(SQLIntegrityConstraintViolationException ex) {
//        ex.printStackTrace();//在控制台打印错误消息.
//    }
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handleDefaultException(Exception ex){
        ex.printStackTrace();//在控制台打印错误消息.
        return Result.defaultError();
    }
}
