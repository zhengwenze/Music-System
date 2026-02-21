package org.zwz.mod_msg.entity;

import lombok.Data;

@Data
public class MsgResponse {
    private Boolean success;
    private String message;
    private Object data;

    public static MsgResponse success(String message, Object data) {
        MsgResponse response = new MsgResponse();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static MsgResponse success(String message) {
        return success(message, null);
    }

    public static MsgResponse error(String message) {
        MsgResponse response = new MsgResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}