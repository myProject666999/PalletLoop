package com.palletloop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.palletloop.entity.EquipmentType;
import com.palletloop.mapper.EquipmentTypeMapper;
import com.palletloop.service.EquipmentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentTypeServiceImpl implements EquipmentTypeService {

    private final EquipmentTypeMapper equipmentTypeMapper;

    @Override
    public IPage<EquipmentType> page(int current, int size, String name) {
        Page<EquipmentType> page = new Page<>(current, size);
        LambdaQueryWrapper<EquipmentType> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(EquipmentType::getName, name);
        }
        wrapper.orderByDesc(EquipmentType::getCreateTime);
        return equipmentTypeMapper.selectPage(page, wrapper);
    }

    @Override
    public List<EquipmentType> list() {
        LambdaQueryWrapper<EquipmentType> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(EquipmentType::getCreateTime);
        return equipmentTypeMapper.selectList(wrapper);
    }

    @Override
    public EquipmentType getById(Long id) {
        return equipmentTypeMapper.selectById(id);
    }

    @Override
    public boolean save(EquipmentType entity) {
        return equipmentTypeMapper.insert(entity) > 0;
    }

    @Override
    public boolean updateById(EquipmentType entity) {
        return equipmentTypeMapper.updateById(entity) > 0;
    }

    @Override
    public boolean removeById(Long id) {
        return equipmentTypeMapper.deleteById(id) > 0;
    }
}
