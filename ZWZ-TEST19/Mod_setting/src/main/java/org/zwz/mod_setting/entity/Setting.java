package org.zwz.mod_setting.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("t_setting")
public class Setting {

    private String id;
    private String key;
    private String value;
    private String description;
}