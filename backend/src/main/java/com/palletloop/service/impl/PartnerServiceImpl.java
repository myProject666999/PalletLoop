package com.palletloop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.palletloop.entity.Partner;
import com.palletloop.mapper.PartnerMapper;
import com.palletloop.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {

    private final PartnerMapper partnerMapper;

    @Override
    public IPage<Partner> page(int current, int size, String name, String type) {
        Page<Partner> page = new Page<>(current, size);
        LambdaQueryWrapper<Partner> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Partner::getName, name);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(Partner::getType, type);
        }
        wrapper.orderByDesc(Partner::getCreateTime);
        return partnerMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Partner> list() {
        LambdaQueryWrapper<Partner> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Partner::getCreateTime);
        return partnerMapper.selectList(wrapper);
    }

    @Override
    public Partner getById(Long id) {
        return partnerMapper.selectById(id);
    }

    @Override
    public boolean save(Partner entity) {
        return partnerMapper.insert(entity) > 0;
    }

    @Override
    public boolean updateById(Partner entity) {
        return partnerMapper.updateById(entity) > 0;
    }

    @Override
    public boolean removeById(Long id) {
        return partnerMapper.deleteById(id) > 0;
    }
}
