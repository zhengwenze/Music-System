package org.zwz.mod_login.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.zwz.mod_login.entity.LoginRequest;
import org.zwz.mod_login.entity.Mess;
import org.zwz.mod_login.entity.User;
import org.zwz.mod_login.service.UserService;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Mess register(@Valid @RequestBody User user) {
        return userService.register(user);
    }

    @GetMapping("/activation")
    public String activation(String token) {
        return userService.activation(token);
    }

    @PostMapping("/login")
    public Mess login(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @GetMapping("/getInfo")
    public Mess getInfo(@RequestParam("userId") Integer userId) {
        return userService.getInfo(userId);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Mess handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .findFirst()
                .orElse("参数验证失败");
        return Mess.fail().code(400).message(errorMessage);
    }
}
