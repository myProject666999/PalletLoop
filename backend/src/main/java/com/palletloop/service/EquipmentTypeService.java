package com.palletloop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.palletloop.entity.EquipmentType;

import java.util.List;

public interface EquipmentTypeService {

    IPage<EquipmentType> page(int current, int size, String name);

    List<EquipmentType> list();

    EquipmentType getById(Long id);

    boolean save(EquipmentType entity);

    boolean updateById(EquipmentType entity);

    boolean removeById(Long id);
}
