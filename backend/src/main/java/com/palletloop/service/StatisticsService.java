package com.palletloop.service;

import com.palletloop.dto.StatisticsDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface StatisticsService {

    StatisticsDTO.OverviewStat getOverview();

    List<StatisticsDTO.PartnerStat> getPartnerStats(Long partnerId);

    List<StatisticsDTO.PartnerStat> getAllPartnerStats();

    void exportPartnerStats(Long partnerId, HttpServletResponse response);
}
