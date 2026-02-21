package org.zwz.mod_login.entity;

import lombok.Data;

@Data
public class LoginResponse {
    private Integer id;
    private String username;
    private Integer role;
    private String token;
    private String message;
}
