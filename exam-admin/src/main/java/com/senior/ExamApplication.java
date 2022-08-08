package com.senior;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.stereotype.Controller;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@Controller
public class ExamApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ExamApplication.class, args);
    }

}
