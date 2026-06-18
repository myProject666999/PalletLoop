package com.palletloop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("dispatch_record")
public class DispatchRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("dispatch_no")
    private String dispatchNo;

    @TableField("partner_id")
    private Long partnerId;

    @TableField("equipment_id")
    private Long equipmentId;

    @TableField("type_id")
    private Long typeId;

    @TableField("quantity")
    private Integer quantity;

    @TableField("order_no")
    private String orderNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("dispatch_date")
    private LocalDate dispatchDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("expected_return_date")
    private LocalDate expectedReturnDate;

    @TableField("operator")
    private String operator;

    @TableField("remark")
    private String remark;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    private LocalDateTime updateTime;
}
