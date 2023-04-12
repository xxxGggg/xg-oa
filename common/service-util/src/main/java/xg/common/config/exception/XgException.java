package xg.common.config.exception;

import lombok.Data;
import xg.common.result.ResultCodeEnum;

/**
 * @author XG
 * @create 2023-04-06 20:04
 */

@Data
public class XgException extends RuntimeException{
    private Integer code;//状态码
    private String msg;//描述信息

    public XgException(Integer code,String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public XgException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMessage();
    }

    @Override
    public String toString() {
        return "XgException{" +
                "code=" + code +
                ", message=" + this.getMsg() +
                '}';
    }

}
