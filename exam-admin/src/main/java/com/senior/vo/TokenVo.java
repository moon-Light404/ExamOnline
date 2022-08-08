package com.senior.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenVo {
    private String roleId;
    private String username;
    private String password;
    private String avatar;

    public TokenVo(String roleId, String username, String password) {
        this.roleId = roleId;
        this.username = username;
        this.password = password;
    }
}
