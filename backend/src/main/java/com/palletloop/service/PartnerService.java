package com.palletloop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.palletloop.common.Result;
import com.palletloop.entity.Partner;

import java.util.List;

public interface PartnerService {

    IPage<Partner> page(int current, int size, String name, String type);

    List<Partner> list();

    Partner getById(Long id);

    Result<?> save(Partner entity);

    Result<?> updateById(Partner entity);

    boolean removeById(Long id);
}
