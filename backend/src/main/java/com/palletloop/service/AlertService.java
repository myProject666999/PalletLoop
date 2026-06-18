package com.palletloop.service;

import com.palletloop.dto.OverdueAlertDTO;

import java.util.List;

public interface AlertService {

    List<OverdueAlertDTO> getOverdueAlerts();

    List<OverdueAlertDTO> getOverdueAlertsByPartner(Long partnerId);
}
