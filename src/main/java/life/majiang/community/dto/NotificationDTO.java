package life.majiang.community.dto;

import lombok.Data;


@Data
public class NotificationDTO {

    private Long id;
    private Long gmtCreate;
    private Integer status;//回复状态
    private Long notifier;//回复人Id
    private String notifierName;//回复人姓名
    private Long outerid;//被回复消息标题/内容 id
    private String outerTitle;//被回复消息标题/内容
    private Integer type;//被回复消息类型
    private String typeName;//被回复消息类型
}
