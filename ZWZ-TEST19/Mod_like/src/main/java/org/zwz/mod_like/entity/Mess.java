package org.zwz.mod_like.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Mess implements Serializable {
    //统一使用驼峰命名法，第一个单词小写，后面相连的单词间要用大写字母隔开
    //统一使用驼峰命名法，第一个单词小写，后面相连的单词间要用大写字母隔开
    //统一使用驼峰命名法，第一个单词小写，后面相连的单词间要用大写字母隔开

    private Integer code;

    private String message;

    private boolean success;

    private Map<String, Object> data = new HashMap<>();

    //成功返回
    public static Mess success() {
        Mess mess = new Mess();
        mess.setCode(20);
        mess.setMessage("响应成功");
        mess.setSuccess(true);
        return mess;
    }

    //失败返回
    public static Mess fail() {
        Mess mess = new Mess();
        mess.setCode(500);
        mess.setMessage("失败");
        mess.setSuccess(false);
        return mess;
    }

    public Mess data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public Mess code(Integer code) {
        this.code = code;
        return this;
    }

    public Mess message(String message) {
        this.message = message;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public Mess setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Mess setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public Mess setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Mess setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public Mess mess(String message) {
        this.message = message;
        return this;
    }
}