package io.github.tml.mosaic.core.execption;

/**
 * 描述: 方块异常
 * @author suifeng
 * 日期: 2025/5/29
 */
public class CubeException extends RuntimeException{

    private int errCode = 500;

    public CubeException(String message){
        super(message);
    }

    public CubeException(String message, int errCode){
        super(message);
        this.errCode = errCode;
    }

    public int getErrCode() {
        return errCode;
    }

    public CubeException(Throwable cause)
    {
        super(cause);
    }

    public CubeException(String message,Throwable cause)
    {
        super(message,cause);
    }

    public static CubeException cubeNotExistException(String cubeId){
        return new CubeException("cubeId:"+cubeId+"not exist");
    }
}
