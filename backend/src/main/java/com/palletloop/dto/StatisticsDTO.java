package com.palletloop.dto;

import lombok.Data;

import java.math.BigDecimal;

public class StatisticsDTO {

    @Data
    public static class PartnerStat {
        private Long partnerId;
        private String partnerName;
        private Long typeId;
        private String typeName;
        private Integer dispatched;
        private Integer returned;
        private Integer damaged;
        private Integer outstanding;
        private Integer turnoverCount;
        private BigDecimal lossRate;
    }

    @Data
    public static class OverviewStat {
        private Integer totalEquipment;
        private Integer totalInStock;
        private Integer totalOutStock;
        private Integer totalPartners;
        private Integer totalOverdue;
        private Integer totalOutstanding;
    }
}
