package life.majiang.community.service;

import life.majiang.community.dto.UCloudFileDTO;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findByAccountId(Long accountId) {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(accountId);
        List<User> users = userMapper.selectByExample(userExample);
        return users.size() == 0 ? null : users.get(0);
    }

    public User findById(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    public void createOrUpdate(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);

        if (users.size() != 0) {
            User dbUser = users.get(0);
            User updateUser = new User();
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setName(user.getName());
            updateUser.setToken(user.getToken());
            updateUser.setBio(user.getBio());
            updateUser.setPassword(user.getPassword());
            userExample.createCriteria().andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(updateUser, userExample);

        } else {
            //没有 则插入
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setUnreadCount(0L);
            userMapper.insert(user);

        }
    }

    public void updateAvatar(Long userId, UCloudFileDTO uCloudFileDTO) {
        User user = new User();
        user.setId(userId);
        user.setAvatarUrl(uCloudFileDTO.getFileUrl());
        user.setAvatarName(uCloudFileDTO.getFileName());
        userMapper.updateByPrimaryKeySelective(user);
    }
}

