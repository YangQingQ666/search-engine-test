package cn.mpy.estestdemo.api;

/**
 * 封装API的错误码
 * @author Admin
 */
public interface IErrorCode {
    /**
     * 获取结果码
     * @return
     */
    long getCode();

    /**
     * 获取返回的信息
     * @return
     */
    String getMessage();
}
