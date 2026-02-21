package org.zwz.mod_like.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户喜欢表
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("mylike")
public class MyLike {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    @TableField(value = "user")
    private Integer userId;
    
    @TableField(value = "music")
    private Integer musicId;
    
    @TableField(exist = false)
    private Integer songListId;
    
    @TableField(value = "create_time")
    private LocalDateTime createTime;
}
