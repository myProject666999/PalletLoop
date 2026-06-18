package com.palletloop.controller;

import com.palletloop.common.Result;
import com.palletloop.dto.OverdueAlertDTO;
import com.palletloop.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/overdue")
    public Result<List<OverdueAlertDTO>> getOverdueAlerts() {
        return Result.success(alertService.getOverdueAlerts());
    }

    @GetMapping("/overdue/{partnerId}")
    public Result<List<OverdueAlertDTO>> getOverdueAlertsByPartner(@PathVariable Long partnerId) {
        return Result.success(alertService.getOverdueAlertsByPartner(partnerId));
    }
}
