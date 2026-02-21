package org.zwz.mod_login.service;

import org.zwz.mod_login.entity.LoginResponse;
import org.zwz.mod_login.entity.User;

public interface UserLoginService {
    LoginResponse login(String username, String password);
    User getUserByUsername(String username);
    boolean verifyPassword(String inputPassword, String storedPassword);
}
