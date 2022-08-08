package com.senior.util;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.util.DigestUtils;

import com.senior.entity.Answer;

public class SaltEncryption {

    // 盐值加密
    public static String saltEncryption(String password, String salt) throws NoSuchAlgorithmException {
        String current = password + salt;
        return DigestUtils.md5DigestAsHex(current.getBytes());
    }

    // 根据题目id获取答案列表中的答案索引
    public static int getIndex(List<Answer> list, Integer questionId) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getQuestionId() == questionId) {
                return i;
            }
        }
        return -1;
    }
}
