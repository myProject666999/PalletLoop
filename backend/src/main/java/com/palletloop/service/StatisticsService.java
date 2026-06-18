package com.palletloop.service;

import com.palletloop.dto.StatisticsDTO;

import java.util.List;

public interface StatisticsService {

    StatisticsDTO.OverviewStat getOverview();

    List<StatisticsDTO.PartnerStat> getPartnerStats(Long partnerId);

    List<StatisticsDTO.PartnerStat> getAllPartnerStats();
}
