package com.palletloop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DispatchDTO {

    @NotNull
    private Long partnerId;

    @NotNull
    private Long equipmentId;

    @NotNull
    private Long typeId;

    @NotNull
    private Integer quantity;

    private String orderNo;

    @NotNull
    private LocalDate dispatchDate;

    private LocalDate expectedReturnDate;

    private String operator;

    private String remark;
}
