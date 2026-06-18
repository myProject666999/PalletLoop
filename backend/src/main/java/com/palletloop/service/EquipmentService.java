package com.palletloop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.palletloop.entity.Equipment;

public interface EquipmentService {

    IPage<Equipment> page(int current, int size, Long typeId);

    Equipment getById(Long id);

    boolean save(Equipment entity);

    boolean updateById(Equipment entity);
}
