package com.palletloop.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OverdueAlertDTO {

    private Long dispatchId;
    private String dispatchNo;
    private Long partnerId;
    private String partnerName;
    private Long typeId;
    private String typeName;
    private Integer quantity;
    private LocalDate dispatchDate;
    private LocalDate expectedReturnDate;
    private Integer overdueDays;
}
