package com.palletloop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.palletloop.common.Result;
import com.palletloop.dto.DispatchDTO;
import com.palletloop.entity.DispatchRecord;
import com.palletloop.entity.Equipment;
import com.palletloop.entity.PartnerBalance;
import com.palletloop.mapper.DispatchRecordMapper;
import com.palletloop.mapper.EquipmentMapper;
import com.palletloop.mapper.PartnerBalanceMapper;
import com.palletloop.service.DispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class DispatchServiceImpl implements DispatchService {

    private final DispatchRecordMapper dispatchRecordMapper;
    private final EquipmentMapper equipmentMapper;
    private final PartnerBalanceMapper partnerBalanceMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public IPage<DispatchRecord> page(int current, int size, Long partnerId, Long typeId, String dispatchNo) {
        Page<DispatchRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<DispatchRecord> wrapper = new LambdaQueryWrapper<>();
        if (partnerId != null) {
            wrapper.eq(DispatchRecord::getPartnerId, partnerId);
        }
        if (typeId != null) {
            wrapper.eq(DispatchRecord::getTypeId, typeId);
        }
        if (StringUtils.hasText(dispatchNo)) {
            wrapper.like(DispatchRecord::getDispatchNo, dispatchNo);
        }
        wrapper.orderByDesc(DispatchRecord::getCreateTime);
        return dispatchRecordMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public Result<?> dispatch(DispatchDTO dto) {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String seq = String.format("%04d", System.currentTimeMillis() % 10000);
        String dispatchNo = "DP" + dateStr + seq;

        DispatchRecord record = new DispatchRecord();
        record.setDispatchNo(dispatchNo);
        record.setPartnerId(dto.getPartnerId());
        record.setEquipmentId(dto.getEquipmentId());
        record.setTypeId(dto.getTypeId());
        record.setQuantity(dto.getQuantity());
        record.setOrderNo(dto.getOrderNo());
        record.setDispatchDate(dto.getDispatchDate());
        record.setExpectedReturnDate(dto.getExpectedReturnDate());
        record.setOperator(dto.getOperator());
        record.setRemark(dto.getRemark());
        dispatchRecordMapper.insert(record);

        Equipment equipment = equipmentMapper.selectById(dto.getEquipmentId());
        if (equipment == null) {
            return Result.error("设备不存在");
        }
        equipment.setOutStock(equipment.getOutStock() + dto.getQuantity());
        equipment.setInStock(equipment.getInStock() - dto.getQuantity());
        equipmentMapper.updateById(equipment);

        LambdaQueryWrapper<PartnerBalance> balanceWrapper = new LambdaQueryWrapper<>();
        balanceWrapper.eq(PartnerBalance::getPartnerId, dto.getPartnerId())
                .eq(PartnerBalance::getTypeId, dto.getTypeId());
        PartnerBalance balance = partnerBalanceMapper.selectOne(balanceWrapper);
        if (balance == null) {
            balance = new PartnerBalance();
            balance.setPartnerId(dto.getPartnerId());
            balance.setTypeId(dto.getTypeId());
            balance.setDispatched(dto.getQuantity());
            balance.setReturned(0);
            balance.setDamaged(0);
            balance.setOutstanding(dto.getQuantity());
            balance.setTurnoverCount(1);
            balance.setLastDispatchDate(dto.getDispatchDate());
            partnerBalanceMapper.insert(balance);
        } else {
            balance.setDispatched(balance.getDispatched() + dto.getQuantity());
            balance.setOutstanding(balance.getOutstanding() + dto.getQuantity());
            balance.setTurnoverCount(balance.getTurnoverCount() + 1);
            balance.setLastDispatchDate(dto.getDispatchDate());
            partnerBalanceMapper.updateById(balance);
        }

        String balanceKey = "balance:" + dto.getPartnerId() + ":" + dto.getTypeId();
        redisTemplate.delete(balanceKey);
        redisTemplate.delete("balance:all");

        return Result.success(record);
    }

    @Override
    public DispatchRecord getById(Long id) {
        return dispatchRecordMapper.selectById(id);
    }
}
