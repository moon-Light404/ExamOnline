package com.senior.config.Interceptor;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.senior.service.impl.UserServiceImpl;
import com.senior.util.CheckToken;
import com.senior.vo.TokenVo;

// 管理员的拦截器
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Autowired
    private UserServiceImpl userService;

    // 这个方法是在访问接口之前执行的，我们只需要在这里写验证登陆状态的业务逻辑，就可以在用户调用指定接口之前验证登陆状态了
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 判断用户的token信息是否满足
        TokenVo tokenVo = new CheckToken().checkToken(request, userService);
        if (tokenVo != null && Objects.equals(tokenVo.getRoleId(), 3 + "")) {
            return true;
        }
        // 当前不满足条件,直接跳转拦截
        response.getWriter().print("Access denied");
        return false;
    }

}
