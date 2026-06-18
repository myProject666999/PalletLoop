package com.palletloop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.palletloop.common.Result;
import com.palletloop.dto.DispatchDTO;
import com.palletloop.entity.DispatchRecord;

public interface DispatchService {

    IPage<DispatchRecord> page(int current, int size, Long partnerId, Long typeId, String dispatchNo);

    Result<?> dispatch(DispatchDTO dto);

    DispatchRecord getById(Long id);
}
