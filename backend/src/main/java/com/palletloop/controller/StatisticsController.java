package com.palletloop.controller;

import com.palletloop.common.Result;
import com.palletloop.dto.StatisticsDTO;
import com.palletloop.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/overview")
    public Result<StatisticsDTO.OverviewStat> getOverview() {
        return Result.success(statisticsService.getOverview());
    }

    @GetMapping("/partner/{partnerId}")
    public Result<List<StatisticsDTO.PartnerStat>> getPartnerStats(@PathVariable Long partnerId) {
        return Result.success(statisticsService.getPartnerStats(partnerId));
    }

    @GetMapping("/partners")
    public Result<List<StatisticsDTO.PartnerStat>> getAllPartnerStats() {
        return Result.success(statisticsService.getAllPartnerStats());
    }
}
