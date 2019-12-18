package life.majiang.community.exception;

public class CustomizeException extends RuntimeException{

    private  Integer code;
    private String message;

    public CustomizeException(ICustomizeErrorCode errorCode) {

        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
