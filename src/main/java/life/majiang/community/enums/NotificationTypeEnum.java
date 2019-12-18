package life.majiang.community.enums;

/**
 * 回复的状态类型
 */
public enum NotificationTypeEnum {

    UNREAD(0),
    READ(1);

    private Integer status;

    NotificationTypeEnum(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

}
