package org.zwz.mod_msg.entity;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class MsgRequest {

    @NotNull(message = "发送者ID不能为空")
    private Long senderId;

    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;

    @NotNull(message = "消息类型不能为空")
    private Integer messageType;

    @NotBlank(message = "消息内容不能为空")
    private String content;

    private Long relatedId;

    private Integer relatedType;
}