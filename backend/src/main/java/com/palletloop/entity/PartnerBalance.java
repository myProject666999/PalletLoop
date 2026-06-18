package com.palletloop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("partner_balance")
public class PartnerBalance {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("partner_id")
    private Long partnerId;

    @TableField("type_id")
    private Long typeId;

    @TableField("dispatched")
    private Integer dispatched;

    @TableField("returned")
    private Integer returned;

    @TableField("damaged")
    private Integer damaged;

    @TableField("outstanding")
    private Integer outstanding;

    @TableField("turnover_count")
    private Integer turnoverCount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("last_dispatch_date")
    private LocalDate lastDispatchDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("last_return_date")
    private LocalDate lastReturnDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    private LocalDateTime updateTime;
}
