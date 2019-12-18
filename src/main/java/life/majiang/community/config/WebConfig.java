package life.majiang.community.config;

import life.majiang.community.interceptor.SessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SessionInterceptor sessionInterceptor;

    //登陆检查
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor).addPathPatterns("/**");

    }


//    //静态资源文件拦截配置
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry){
////        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
////        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
////        registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/static/fonts/");
//    }

}
