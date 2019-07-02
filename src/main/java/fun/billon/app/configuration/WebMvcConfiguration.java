package fun.billon.app.configuration;

import fun.billon.auth.api.feign.IAuthService;
import fun.billon.auth.api.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * webmvc配置
 *
 * @author billon
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * 内部服务id(sid)
     */
    @Value("${billon.app-web.sid}")
    private String sid;

    /**
     * auth
     */
    @Autowired
    private IAuthService authService;

    /**
     * token拦截器
     */
    @Bean
    public TokenInterceptor tokenInterceptor() {
        TokenInterceptor tokenInterceptor = new TokenInterceptor();
        tokenInterceptor.setAuthService(authService);
        tokenInterceptor.setSid(sid);
        return tokenInterceptor;
    }

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/error")
                .excludePathPatterns("/actuator/**")
                .excludePathPatterns("/logger/**")
                .excludePathPatterns("/auth/token");
    }

}