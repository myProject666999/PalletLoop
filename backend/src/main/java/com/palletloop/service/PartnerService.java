package com.palletloop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.palletloop.entity.Partner;

import java.util.List;

public interface PartnerService {

    IPage<Partner> page(int current, int size, String name, String type);

    List<Partner> list();

    Partner getById(Long id);

    boolean save(Partner entity);

    boolean updateById(Partner entity);

    boolean removeById(Long id);
}
