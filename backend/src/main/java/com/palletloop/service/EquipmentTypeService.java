package com.palletloop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.palletloop.common.Result;
import com.palletloop.entity.EquipmentType;

import java.util.List;

public interface EquipmentTypeService {

    IPage<EquipmentType> page(int current, int size, String name);

    List<EquipmentType> list();

    EquipmentType getById(Long id);

    Result<?> save(EquipmentType entity);

    Result<?> updateById(EquipmentType entity);

    boolean removeById(Long id);
}
