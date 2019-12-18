package life.majiang.community.controller;

import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.QueryDTO;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.service.NotificationService;
import life.majiang.community.service.QuestionService;
import life.majiang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action") String action,
                          HttpServletRequest request,
                          Model model,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "5") Integer size
    ) {

        QueryDTO queryDTO = new QueryDTO(page, size);
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            //返回未登录提示
            //跳转回首页
            return "redirect:/";
        }
        //我的问题或者个人中心  进入我的问题
        if ("questions".equals(action)) {
            //获取我的问题
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
            PaginationDTO pagination = questionService.getQuestionsByUserId(user.getId(), queryDTO);
            model.addAttribute("pagination", pagination);
        } else if ("replies".equals(action)) {
            //获取我的消息
            PaginationDTO pagination = notificationService.list(user.getId(), queryDTO);
//            Long unreadCount=notificationService.unreadCount(user.getId());
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "最新回复");
            model.addAttribute("pagination", pagination);
        } else if ("info".equals(action) || "index".equals(action)) {
            model.addAttribute("section", "info");
            model.addAttribute("sectionName", "个人资料");
            model.addAttribute("user", user);
        }

        return "profile";
    }


    @GetMapping("/other/{id}")
    public String others(@PathVariable("id")Long id,
                         Model model){

        User otherUser = userService.findById(id);
        model.addAttribute("user",otherUser);
        return "others";

    }
}
