package com.palletloop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.palletloop.common.Result;
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
    public Result<?> save(EquipmentType entity) {
        if (!StringUtils.hasText(entity.getCode())) {
            return Result.error("类型编码不能为空");
        }
        LambdaQueryWrapper<EquipmentType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquipmentType::getCode, entity.getCode());
        Long count = equipmentTypeMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            return Result.error("类型编码已存在：" + entity.getCode());
        }
        return equipmentTypeMapper.insert(entity) > 0 ? Result.success() : Result.error("保存失败");
    }

    @Override
    public Result<?> updateById(EquipmentType entity) {
        if (!StringUtils.hasText(entity.getCode())) {
            return Result.error("类型编码不能为空");
        }
        LambdaQueryWrapper<EquipmentType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquipmentType::getCode, entity.getCode())
                .ne(entity.getId() != null, EquipmentType::getId, entity.getId());
        Long count = equipmentTypeMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            return Result.error("类型编码已存在：" + entity.getCode());
        }
        return equipmentTypeMapper.updateById(entity) > 0 ? Result.success() : Result.error("更新失败");
    }

    @Override
    public boolean removeById(Long id) {
        return equipmentTypeMapper.deleteById(id) > 0;
    }
}
