package com.palletloop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.palletloop.dto.OverdueAlertDTO;
import com.palletloop.entity.DispatchRecord;
import com.palletloop.entity.EquipmentType;
import com.palletloop.entity.Partner;
import com.palletloop.entity.ReturnRecord;
import com.palletloop.mapper.DispatchRecordMapper;
import com.palletloop.mapper.EquipmentTypeMapper;
import com.palletloop.mapper.PartnerMapper;
import com.palletloop.mapper.ReturnRecordMapper;
import com.palletloop.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final DispatchRecordMapper dispatchRecordMapper;
    private final ReturnRecordMapper returnRecordMapper;
    private final PartnerMapper partnerMapper;
    private final EquipmentTypeMapper equipmentTypeMapper;

    @Override
    public List<OverdueAlertDTO> getOverdueAlerts() {
        return buildOverdueAlerts(null);
    }

    @Override
    public List<OverdueAlertDTO> getOverdueAlertsByPartner(Long partnerId) {
        return buildOverdueAlerts(partnerId);
    }

    private List<OverdueAlertDTO> buildOverdueAlerts(Long partnerId) {
        LocalDate today = LocalDate.now();
        List<OverdueAlertDTO> alerts = new ArrayList<>();

        LambdaQueryWrapper<DispatchRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(DispatchRecord::getExpectedReturnDate)
                .lt(DispatchRecord::getExpectedReturnDate, today);
        if (partnerId != null) {
            wrapper.eq(DispatchRecord::getPartnerId, partnerId);
        }
        List<DispatchRecord> overdueRecords = dispatchRecordMapper.selectList(wrapper);

        for (DispatchRecord dispatch : overdueRecords) {
            LambdaQueryWrapper<ReturnRecord> returnWrapper = new LambdaQueryWrapper<>();
            returnWrapper.eq(ReturnRecord::getDispatchId, dispatch.getId());
            List<ReturnRecord> returns = returnRecordMapper.selectList(returnWrapper);

            int totalReturned = returns.stream()
                    .mapToInt(ReturnRecord::getQuantity)
                    .sum();

            if (totalReturned < dispatch.getQuantity()) {
                OverdueAlertDTO dto = new OverdueAlertDTO();
                dto.setDispatchId(dispatch.getId());
                dto.setDispatchNo(dispatch.getDispatchNo());
                dto.setPartnerId(dispatch.getPartnerId());
                dto.setTypeId(dispatch.getTypeId());
                dto.setQuantity(dispatch.getQuantity());
                dto.setDispatchDate(dispatch.getDispatchDate());
                dto.setExpectedReturnDate(dispatch.getExpectedReturnDate());
                dto.setOverdueDays((int) ChronoUnit.DAYS.between(dispatch.getExpectedReturnDate(), today));

                Partner partner = partnerMapper.selectById(dispatch.getPartnerId());
                dto.setPartnerName(partner != null ? partner.getName() : "未知合作方");

                EquipmentType type = equipmentTypeMapper.selectById(dispatch.getTypeId());
                dto.setTypeName(type != null ? type.getName() : "未知类型");

                alerts.add(dto);
            }
        }

        return alerts;
    }
}
