package life.majiang.community.service;

import life.majiang.community.dto.CommentDTO;
import life.majiang.community.enums.CommentTypeEnum;
import life.majiang.community.enums.NotificationEnum;
import life.majiang.community.enums.NotificationTypeEnum;
import life.majiang.community.enums.OrderByEnum;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.mapper.*;
import life.majiang.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserExtMapper userExtMapper;

    @Autowired
    private NotificationMapper notificationMapper;


    @Transactional
    public void insert(Comment comment, User user) {

        if (comment.getParentId() == null || Objects.equals(comment.getParentId(),0)) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }

        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        if (Objects.equals(comment.getType(), CommentTypeEnum.COMMENT.getType())) {
            //回复评论

            //得到被回复的评论
            Comment parentComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (parentComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);

            //增加父评论评论数
            parentComment.setCommentCount(1);
            commentExtMapper.incCommentCount(parentComment);

            //增加父问题评论数
            Question question = new Question();
            question.setId(parentComment.getParentId());
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);

            //如果不是回复自己
            if (!Objects.equals(user.getId(), question.getCreator())) {

                //创建通知
                createNotification(user, parentComment.getCommentator(), parentComment.getParentId(), parentComment.getContent(), NotificationEnum.REPLY_COMMENT.getStatus());

                //增加被回复人通知数
                addNotificationToReceiver(parentComment.getCommentator());
            }
        } else {
            //回复问题

            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            } else {
                commentMapper.insert(comment);

                //回复之后评论数增加
                question.setCommentCount(1);
                questionExtMapper.incCommentCount(question);

                //如果不是回复自己
                if (!Objects.equals(user.getId(), question.getCreator())) {
                    //创建通知
                    createNotification(user, question.getCreator(), question.getId(), question.getTitle(), NotificationEnum.REPLY_COMMENT.getStatus());

                    //增加被回复人通知数
                    addNotificationToReceiver(question.getCreator());
                }
            }
        }
    }

    /**
     * @param receiverId 被回复人Id
     */
    public void addNotificationToReceiver(Long receiverId) {
        //增加被回复人通知数
        User receiver = new User();
        receiver.setId(receiverId);
        receiver.setUnreadCount(1L);
        userExtMapper.incUnreadCount(receiver);
    }

    /**
     * 创建消息
     *
     * @param user       发送人
     * @param receiver   接收人id
     * @param receiveId  接收物id(问题|评论)
     * @param outerTitle 接收物标题(问题|评论)
     * @param type       类型(回复问题:1|回复评论:2)
     */
    private void createNotification(User user, Long receiver, Long receiveId, String outerTitle, Integer type) {
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setNotifier(user.getId());
        notification.setReceiver(receiver);
        notification.setStatus(NotificationTypeEnum.UNREAD.getStatus());
        notification.setType(type);
        notification.setOuterid(receiveId);
        notification.setNotifierName(user.getName());
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);

    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {

        CommentExample commentExample = new CommentExample();

        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        commentExample.setOrderByClause(OrderByEnum.ORDER_BY_GMT_MODIFIED.getSort());
        //根据评论类型和  问题Id 得到评论列表
        List<Comment> comments = commentMapper.selectByExample(commentExample);

        if (comments.size() == 0) {
            //如果评论列表未空  直接返回空List
            return new ArrayList<>();

        }

        //根据评论的发布人Id得到发布人Id Set集合(无重复Id)
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());

        //将发布人Id由Set集合变为用户Id List集合
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);

        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        //通过用户Id集合查找到用户列表
        List<User> users = userMapper.selectByExample(userExample);
        //将用户列表设置为 id:user 的键值对Map
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));
        //遍历得到的评论列表
        List<CommentDTO> commentDTOs = comments.stream().map(comment -> {
            //封装评论
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            //根据userMap中的映射取到User
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOs;
    }

}
