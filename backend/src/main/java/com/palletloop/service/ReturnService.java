package com.palletloop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.palletloop.common.Result;
import com.palletloop.dto.ReturnDTO;
import com.palletloop.entity.ReturnRecord;

public interface ReturnService {

    IPage<ReturnRecord> page(int current, int size, Long partnerId, Long typeId, String returnNo);

    Result<?> returnEquipment(ReturnDTO dto);

    ReturnRecord getById(Long id);
}
