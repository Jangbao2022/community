package life.majiang.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode {

    INPUT_ERROR(2000, "输入有误"),
    QUESTION_NOT_FOUND(2001, "你要找的问题不在了,要不换个试试"),
    TARGET_PARAM_NOT_FOUND(2002, "未选中任何问题或评论进行回复"),
    NO_LOGIN(2003, "当前操作需要登陆,请先登陆"),
    SYSTEM_ERROR(2004, "服务器冒烟了,请稍后再试"),
    TYPE_PARAM_WRONG(2005, "评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006, "评论不存在"),
    CONTENT_IS_EMPTY(2007, "输入评论内容不能为空"),
    READ_NOTIFICATION_FAIL(2008, "你咋读别人消息了 兄得"),
    NOTIFICATION_NOT_FOUND(2009, "消息不存在啦"),
    IMG_UPLOAD_FAIL(2010, "图片上传失败"),
    ;

    private Integer code;
    private String message;

    CustomizeErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
