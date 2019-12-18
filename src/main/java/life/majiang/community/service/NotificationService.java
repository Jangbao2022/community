package life.majiang.community.service;

import life.majiang.community.dto.NotificationDTO;
import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.QueryDTO;
import life.majiang.community.enums.NotificationEnum;
import life.majiang.community.enums.NotificationTypeEnum;
import life.majiang.community.enums.OrderByEnum;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.mapper.NotificationMapper;
import life.majiang.community.mapper.UserExtMapper;
import life.majiang.community.model.Notification;
import life.majiang.community.model.NotificationExample;
import life.majiang.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserExtMapper userExtMapper;

    /**
     * 得到消息列表
     *
     * @param userId
     * @param queryDTO
     * @return
     */
    public PaginationDTO list(Long userId, QueryDTO queryDTO) {

        //封装notification页面的类
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();

        //查出所有receiver是我的数量
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId);

        Integer totalCount = (int) notificationMapper.countByExample(notificationExample);
        if(totalCount==0){
            return null;
        }
        Integer offset = paginationDTO.setPageOffset(queryDTO, totalCount);

        //得到封装过的notification数组 接收人是我的notification
        notificationExample.setOrderByClause(
                OrderByEnum.ORDER_BY_ENUM//自定义排序
                        .addSort(OrderByEnum.ORDER_BY_STATUS.getSort())//先按读取状态排序
                        .addSort(OrderByEnum.ORDER_BY_GMT_CREATE.getSort())//再按创建时间排序
                        .getSort());
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(notificationExample, new RowBounds(offset, queryDTO.getSize()));
        if (notifications.size() == 0) {
            return null;
        }
        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        //循环根据所有notification查出作者  并封装到questionDto中
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationEnum.nameOfType(notificationDTO.getType()));
            notificationDTOS.add(notificationDTO);
        }

        //将questionDtoList封装到paginationDto中
        paginationDTO.setData(notificationDTOS);

        return paginationDTO;
    }

    /**
     * 读消息
     *
     * @param id   消息Id
     * @param user 用户
     * @return
     */
    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        } else if (!Objects.equals(notification.getReceiver(), user.getId())) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        //如果该消息未读
        if (Objects.equals(notification.getStatus(), NotificationTypeEnum.UNREAD.getStatus())) {
            //更新消息状态为已读
            notification.setStatus(NotificationTypeEnum.READ.getStatus());
            notificationMapper.updateByPrimaryKey(notification);

            //当前用户消息数减1
            user.setUnreadCount(-1L);
            userExtMapper.incUnreadCount(user);
        }

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationEnum.nameOfType(notificationDTO.getType()));
        return notificationDTO;
    }

//    public Long unreadCount(Long userId) {
//        NotificationExample notificationExample = new NotificationExample();
//        notificationExample.createCriteria()
//                .andReceiverEqualTo(userId);
//        Long unreadCount = notificationMapper.countByExample(notificationExample);
//        return unreadCount;
//    }
}
