package com.palletloop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReturnDTO {

    @NotNull
    private Long dispatchId;

    @NotNull
    private Long partnerId;

    @NotNull
    private Long equipmentId;

    @NotNull
    private Long typeId;

    @NotNull
    private Integer quantity;

    private Integer damaged;

    @NotNull
    private LocalDate returnDate;

    private String operator;

    private String remark;
}
