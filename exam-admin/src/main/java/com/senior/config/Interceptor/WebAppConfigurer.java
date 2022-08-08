package com.senior.config.Interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {

    @Bean
    public AdminInterceptor getAdminInterceptor() {// 只属于管理员
        return new AdminInterceptor();
    }

    @Bean
    public TeacherInterceptor getTeacherInterceptor() {// 属于老师 但是管理员也可以用
        return new TeacherInterceptor();
    }

    @Bean
    public StudentInterceptor getStudentInterceptor() {// 属于学生 但是管理员也可以用
        return new StudentInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 可添加多个
        // 拦截未登录进入超级管理员的界面
        registry.addInterceptor(getAdminInterceptor()).addPathPatterns("/admin/**");
        registry.addInterceptor(getTeacherInterceptor()).addPathPatterns("/teacher/**");
        registry.addInterceptor(getStudentInterceptor()).addPathPatterns("/student/**");
    }

}
