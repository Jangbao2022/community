package life.majiang.community.advice;

import com.alibaba.fastjson.JSON;
import life.majiang.community.dto.ResultDTO;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class CustomsizeExceptionHandler {

    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable ex, Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {

        ResultDTO resultDTO;
        String contentType = request.getContentType();
        if ("application/json".equals(contentType)) {
            //返回json
            if (ex instanceof CustomizeException) {
                //如果是客户的异常
                resultDTO = ResultDTO.errorOf((CustomizeException) ex);

            } else {

                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYSTEM_ERROR);
            }

            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();

            } catch (IOException ioe) {

            }
            return null;

        } else {
            //错误页面跳转

            if (ex instanceof CustomizeException) {
                //如果是客户的异常
                model.addAttribute("errorMsg", ex.getMessage());

            } else {
                model.addAttribute("errorMsg", CustomizeErrorCode.SYSTEM_ERROR.getMessage());

            }
            return new ModelAndView("error");

        }
    }
}
