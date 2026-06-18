package com.palletloop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.palletloop.entity.Equipment;
import com.palletloop.mapper.EquipmentMapper;
import com.palletloop.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentMapper equipmentMapper;

    @Override
    public IPage<Equipment> page(int current, int size, Long typeId) {
        Page<Equipment> page = new Page<>(current, size);
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        if (typeId != null) {
            wrapper.eq(Equipment::getTypeId, typeId);
        }
        wrapper.orderByDesc(Equipment::getCreateTime);
        return equipmentMapper.selectPage(page, wrapper);
    }

    @Override
    public Equipment getById(Long id) {
        return equipmentMapper.selectById(id);
    }

    @Override
    public boolean save(Equipment entity) {
        entity.setInStock(entity.getQuantity());
        entity.setOutStock(0);
        return equipmentMapper.insert(entity) > 0;
    }

    @Override
    public boolean updateById(Equipment entity) {
        return equipmentMapper.updateById(entity) > 0;
    }
}
