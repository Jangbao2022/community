package life.majiang.community.mapper;

import life.majiang.community.model.User;

public interface UserExtMapper {

    int incUnreadCount(User record);
}