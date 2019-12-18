package life.majiang.community.controller;

import life.majiang.community.dto.NotificationDTO;
import life.majiang.community.enums.NotificationEnum;
import life.majiang.community.model.Notification;
import life.majiang.community.model.User;
import life.majiang.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(@PathVariable(name = "id") Long id,
                          HttpServletRequest request) {

        User user = (User)request.getSession().getAttribute("user");
        if(user==null){
            //返回未登录提示

            //跳转回首页
            return "redirect:/";
        }
        NotificationDTO notificationDTO=notificationService.read(id,user);
        if(notificationDTO.getType()== NotificationEnum.REPLY_QUESTION.getStatus()){
            return "redirect:/question/"+notificationDTO.getOuterid();
        }else if (notificationDTO.getType()== NotificationEnum.REPLY_COMMENT.getStatus()){
            return "redirect:/question/"+notificationDTO.getOuterid();
        }

        return "profile";
    }

}
