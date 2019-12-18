package life.majiang.community.controller;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import life.majiang.community.model.User;
import life.majiang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Controller
public class LogonController {

    @Autowired
    private UserService userService;

    /**
     * 跳转注册页面
     *
     * @return
     */
    @GetMapping("/logon")
    public String logon(@RequestParam(name = "id", required = false) Long id,
                        Model model) {
        if (id != null) {
            User user = userService.findById(id);
            model.addAttribute("id", user.getId());
            model.addAttribute("accountId", user.getAccountId());
            model.addAttribute("password", user.getPassword());
            model.addAttribute("confirmPassword", user.getPassword());
            model.addAttribute("name", user.getName());
            model.addAttribute("bio", user.getBio());
            model.addAttribute("type", user.getType());
            model.addAttribute("token", user.getToken());
        }
        return "logon";
    }


    /**
     * 注册或者修改账号
     *
     * @return
     */
    @PostMapping("/logon")
    public String doLogon(@RequestParam("accountId") Long accountId,
                          @RequestParam("password") String password,
                          @RequestParam("confirmPassword") String confirmPassword,
                          @RequestParam("name") String name,
                          @RequestParam("bio") String bio,
                          @RequestParam("type") Integer type,
                          @RequestParam("token") String token,
                          @RequestParam("id") Long id,
                          Model model) {
        int logonFlag;//注册 情况标志
        if (!confirmPassword.equals(password)) {
            logonFlag = 1;//第一种情况   确认密码错误
            model.addAttribute("msg", "两次输入密码不一致");
        } else {
            //确认密码正确
            User user = new User();
            User existsUser = userService.findByAccountId(accountId);
            //如果账号已存在
            if (existsUser != null && id == null) {
                //注册账号 且账号已存在
                logonFlag = 2;//第二种情况  注册账号已存在
                model.addAttribute("msg", "该账号已存在,换个试试吧");
            } else {
                //修改账号  或注册账号并账号不存在
                logonFlag = 3;//第三种情况  注册或者修改成功
                user.setAccountId(accountId);
                user.setName(name);
                user.setPassword(password);
                user.setType(type);
                user.setBio(bio);
                user.setToken(token);
                user.setGmtCreate(System.currentTimeMillis());
                user.setGmtModified(user.getGmtCreate());
                userService.createOrUpdate(user);
            }
        }
        if (Objects.equals(logonFlag, 1) || Objects.equals(logonFlag, 2)) {
            model.addAttribute("accountId", accountId);
            model.addAttribute("password", password);
            model.addAttribute("confirmPassword", confirmPassword);
            model.addAttribute("name", name);
            model.addAttribute("bio", bio);
            model.addAttribute("type", type);
            model.addAttribute("token", token);
            model.addAttribute("id",id);
            return "logon";
        }
        return "redirect:/logon/success";
    }

    /**
     * 转到登陆成功页面
     *
     * @return
     */
    @GetMapping("/logon/success")
    public String logon_success() {
        return "logon_success";
    }


    /**
     * 转到登陆页面
     *
     * @return
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 登陆账号
     *
     * @return
     */
    @PostMapping("/login")
    public String doLogin(HttpServletRequest request,
                          HttpServletResponse response,
                          @RequestParam("accountId") Long accountId,
                          @RequestParam("password") String password,
                          Model model) {

        User user = userService.findByAccountId(accountId);
        response.addCookie(new Cookie("accountId", user.getAccountId().toString()));
        return "redirect:/";
    }


    /**
     * 退出登陆
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response) {

        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("accountId", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
}
