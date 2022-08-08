package com.senior.util;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.senior.entity.User;
import com.senior.service.impl.UserServiceImpl;
import com.senior.vo.TokenVo;

public class CheckToken {

    public TokenVo checkToken(HttpServletRequest request, UserServiceImpl userService) {
        // 获取用户的头部信息的token
        String token = request.getHeader("authorization");
        if (token != null) {
            // 获取解析后的token令牌
            TokenVo tokenVo = TokenUtils.verifyToken(token);
            if (tokenVo != null) {// 解析token是否过期
                QueryWrapper<User> wrapper = new QueryWrapper<>();
                wrapper.eq("username", tokenVo.getUsername());
                User user = userService.getOne(wrapper);
                // 校验token是否合法 并且是否过期
                if (tokenVo != null && user != null
                        && user.getRoleId() == Integer.parseInt(tokenVo.getRoleId())
                        && Objects.equals(user.getPassword(), tokenVo.getPassword())
                        && user.getStatus() == 1) {
                    tokenVo.setAvatar(user.getAvatar());
                    return tokenVo;
                } else {// 非法token
                    return null;
                }
            } else {
                return null;
            }
        } else {// 没有token
            return null;
        }
    }
}
