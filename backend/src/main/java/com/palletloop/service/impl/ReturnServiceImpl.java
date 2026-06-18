package com.palletloop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.palletloop.common.Result;
import com.palletloop.dto.ReturnDTO;
import com.palletloop.entity.Equipment;
import com.palletloop.entity.PartnerBalance;
import com.palletloop.entity.ReturnRecord;
import com.palletloop.mapper.EquipmentMapper;
import com.palletloop.mapper.PartnerBalanceMapper;
import com.palletloop.mapper.ReturnRecordMapper;
import com.palletloop.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReturnServiceImpl implements ReturnService {

    private final ReturnRecordMapper returnRecordMapper;
    private final EquipmentMapper equipmentMapper;
    private final PartnerBalanceMapper partnerBalanceMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public IPage<ReturnRecord> page(int current, int size, Long partnerId, Long typeId, String returnNo) {
        Page<ReturnRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<ReturnRecord> wrapper = new LambdaQueryWrapper<>();
        if (partnerId != null) {
            wrapper.eq(ReturnRecord::getPartnerId, partnerId);
        }
        if (typeId != null) {
            wrapper.eq(ReturnRecord::getTypeId, typeId);
        }
        if (StringUtils.hasText(returnNo)) {
            wrapper.like(ReturnRecord::getReturnNo, returnNo);
        }
        wrapper.orderByDesc(ReturnRecord::getCreateTime);
        return returnRecordMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public Result<?> returnEquipment(ReturnDTO dto) {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String seq = String.format("%04d", System.currentTimeMillis() % 10000);
        String returnNo = "RT" + dateStr + seq;

        ReturnRecord record = new ReturnRecord();
        record.setReturnNo(returnNo);
        record.setDispatchId(dto.getDispatchId());
        record.setPartnerId(dto.getPartnerId());
        record.setEquipmentId(dto.getEquipmentId());
        record.setTypeId(dto.getTypeId());
        record.setQuantity(dto.getQuantity());
        record.setDamaged(dto.getDamaged() != null ? dto.getDamaged() : 0);
        record.setReturnDate(dto.getReturnDate());
        record.setOperator(dto.getOperator());
        record.setRemark(dto.getRemark());
        returnRecordMapper.insert(record);

        Equipment equipment = equipmentMapper.selectById(dto.getEquipmentId());
        if (equipment == null) {
            return Result.error("设备不存在");
        }
        int damaged = dto.getDamaged() != null ? dto.getDamaged() : 0;
        equipment.setOutStock(equipment.getOutStock() - (dto.getQuantity() - damaged));
        equipment.setInStock(equipment.getInStock() + (dto.getQuantity() - damaged));
        equipmentMapper.updateById(equipment);

        LambdaQueryWrapper<PartnerBalance> balanceWrapper = new LambdaQueryWrapper<>();
        balanceWrapper.eq(PartnerBalance::getPartnerId, dto.getPartnerId())
                .eq(PartnerBalance::getTypeId, dto.getTypeId());
        PartnerBalance balance = partnerBalanceMapper.selectOne(balanceWrapper);
        if (balance != null) {
            balance.setReturned(balance.getReturned() + dto.getQuantity());
            balance.setDamaged(balance.getDamaged() + damaged);
            balance.setOutstanding(balance.getOutstanding() - dto.getQuantity());
            if (balance.getOutstanding() < 0) {
                balance.setOutstanding(0);
            }
            balance.setLastReturnDate(dto.getReturnDate());
            partnerBalanceMapper.updateById(balance);
        }

        String balanceKey = "balance:" + dto.getPartnerId() + ":" + dto.getTypeId();
        redisTemplate.delete(balanceKey);
        redisTemplate.delete("balance:all");

        return Result.success(record);
    }

    @Override
    public ReturnRecord getById(Long id) {
        return returnRecordMapper.selectById(id);
    }
}
