package life.majiang.community.enums;

public enum UserTypeEnum {

    LOCAL(0),//本地注册的
    GITHUB(1);//github授权登陆的

    private Integer type;

    public Integer getType() {
        return type;
    }

    UserTypeEnum(Integer type) {
        this.type = type;
    }
}
