package com.lcw.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ManGo
 */
@Data
@Accessors(chain = true)
@TableName("formdata")
public class FormData {
    @TableField("PROC_DEF_ID_")
    private String procDefId;
    @TableField("PROC_INST_ID_")
    private String procInstId;
    @TableField("FORM_KEY_")
    private String formKey;
    @TableField("Control_ID_")
    private String controlId;
    @TableField("Control_VALUE_")
    private String controlValue;
}
