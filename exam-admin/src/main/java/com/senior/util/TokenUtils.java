package com.senior.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.senior.vo.TokenVo;

public class TokenUtils {// 过期时间
    private static final long EXPIRE_TIME = 60 * 60 * 1000 * 5;// 默认5小时
    // 私钥
    private static final String TOKEN_SECRET = "TOKEN_SECRET";

    /**
     * 生成token，自定义过期时间 毫秒
     *
     * @param **username**
     * @param **password**
     * @return
     */
    public static String createToken(TokenVo token) {
        try {
            // 设置过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            // 私钥和加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            // 设置头部信息
            Map<String, Object> header = new HashMap<>(2);
            header.put("Type", "Jwt");
            header.put("alg", "HS256");
            // 返回token字符串
            return JWT.create()
                    .withHeader(header)
                    .withClaim("roleId", token.getRoleId())
                    .withClaim("username", token.getUsername())
                    .withClaim("password", token.getPassword())
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 检验token是否正确
     *
     * @param **token**
     * @return
     */
    public static TokenVo verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String roleId = jwt.getClaim("roleId").asString();
            String username = jwt.getClaim("username").asString();
            String password = jwt.getClaim("password").asString();
            return new TokenVo(roleId, username, password);
        } catch (Exception e) {
            return null;
        }
    }
}
