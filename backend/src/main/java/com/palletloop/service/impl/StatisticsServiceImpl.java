package com.palletloop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.palletloop.dto.StatisticsDTO;
import com.palletloop.entity.DispatchRecord;
import com.palletloop.entity.Equipment;
import com.palletloop.entity.EquipmentType;
import com.palletloop.entity.Partner;
import com.palletloop.entity.PartnerBalance;
import com.palletloop.mapper.DispatchRecordMapper;
import com.palletloop.mapper.EquipmentMapper;
import com.palletloop.mapper.EquipmentTypeMapper;
import com.palletloop.mapper.PartnerBalanceMapper;
import com.palletloop.mapper.PartnerMapper;
import com.palletloop.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final EquipmentMapper equipmentMapper;
    private final PartnerBalanceMapper partnerBalanceMapper;
    private final PartnerMapper partnerMapper;
    private final EquipmentTypeMapper equipmentTypeMapper;
    private final DispatchRecordMapper dispatchRecordMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public StatisticsDTO.OverviewStat getOverview() {
        String cacheKey = "stats:overview";
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return (StatisticsDTO.OverviewStat) cached;
        }

        StatisticsDTO.OverviewStat stat = new StatisticsDTO.OverviewStat();

        List<Equipment> equipments = equipmentMapper.selectList(null);
        int totalEquipment = equipments.stream().mapToInt(Equipment::getQuantity).sum();
        int totalInStock = equipments.stream().mapToInt(e -> e.getInStock() != null ? e.getInStock() : 0).sum();
        int totalOutStock = equipments.stream().mapToInt(e -> e.getOutStock() != null ? e.getOutStock() : 0).sum();

        LambdaQueryWrapper<Partner> partnerWrapper = new LambdaQueryWrapper<>();
        int totalPartners = Math.toIntExact(partnerMapper.selectCount(partnerWrapper));

        List<PartnerBalance> balances = partnerBalanceMapper.selectList(null);
        int totalOutstanding = balances.stream().mapToInt(b -> b.getOutstanding() != null ? b.getOutstanding() : 0).sum();

        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<DispatchRecord> overdueWrapper = new LambdaQueryWrapper<>();
        overdueWrapper.lt(DispatchRecord::getExpectedReturnDate, today);
        int totalOverdue = Math.toIntExact(dispatchRecordMapper.selectCount(overdueWrapper));

        stat.setTotalEquipment(totalEquipment);
        stat.setTotalInStock(totalInStock);
        stat.setTotalOutStock(totalOutStock);
        stat.setTotalPartners(totalPartners);
        stat.setTotalOverdue(totalOverdue);
        stat.setTotalOutstanding(totalOutstanding);

        redisTemplate.opsForValue().set(cacheKey, stat, 30, TimeUnit.MINUTES);
        return stat;
    }

    @Override
    public List<StatisticsDTO.PartnerStat> getPartnerStats(Long partnerId) {
        String cacheKey = "stats:partner:" + partnerId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return (List<StatisticsDTO.PartnerStat>) cached;
        }

        LambdaQueryWrapper<PartnerBalance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartnerBalance::getPartnerId, partnerId);
        List<PartnerBalance> balances = partnerBalanceMapper.selectList(wrapper);

        Partner partner = partnerMapper.selectById(partnerId);

        List<StatisticsDTO.PartnerStat> stats = new ArrayList<>();
        for (PartnerBalance balance : balances) {
            StatisticsDTO.PartnerStat stat = new StatisticsDTO.PartnerStat();
            stat.setPartnerId(balance.getPartnerId());
            stat.setPartnerName(partner != null ? partner.getName() : null);
            stat.setTypeId(balance.getTypeId());

            EquipmentType type = equipmentTypeMapper.selectById(balance.getTypeId());
            stat.setTypeName(type != null ? type.getName() : null);

            stat.setDispatched(balance.getDispatched());
            stat.setReturned(balance.getReturned());
            stat.setDamaged(balance.getDamaged());
            stat.setOutstanding(balance.getOutstanding());
            stat.setTurnoverCount(balance.getTurnoverCount());

            if (balance.getDispatched() != null && balance.getDispatched() > 0 && balance.getDamaged() != null) {
                stat.setLossRate(BigDecimal.valueOf(balance.getDamaged())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(balance.getDispatched()), 2, RoundingMode.HALF_UP));
            } else {
                stat.setLossRate(BigDecimal.ZERO);
            }

            stats.add(stat);
        }

        redisTemplate.opsForValue().set(cacheKey, stats, 30, TimeUnit.MINUTES);
        return stats;
    }

    @Override
    public List<StatisticsDTO.PartnerStat> getAllPartnerStats() {
        String cacheKey = "stats:partner:all";
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return (List<StatisticsDTO.PartnerStat>) cached;
        }

        List<PartnerBalance> balances = partnerBalanceMapper.selectList(null);

        List<StatisticsDTO.PartnerStat> stats = new ArrayList<>();
        for (PartnerBalance balance : balances) {
            StatisticsDTO.PartnerStat stat = new StatisticsDTO.PartnerStat();
            stat.setPartnerId(balance.getPartnerId());
            Partner partner = partnerMapper.selectById(balance.getPartnerId());
            stat.setPartnerName(partner != null ? partner.getName() : null);
            stat.setTypeId(balance.getTypeId());

            EquipmentType type = equipmentTypeMapper.selectById(balance.getTypeId());
            stat.setTypeName(type != null ? type.getName() : null);

            stat.setDispatched(balance.getDispatched());
            stat.setReturned(balance.getReturned());
            stat.setDamaged(balance.getDamaged());
            stat.setOutstanding(balance.getOutstanding());
            stat.setTurnoverCount(balance.getTurnoverCount());

            if (balance.getDispatched() != null && balance.getDispatched() > 0 && balance.getDamaged() != null) {
                stat.setLossRate(BigDecimal.valueOf(balance.getDamaged())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(balance.getDispatched()), 2, RoundingMode.HALF_UP));
            } else {
                stat.setLossRate(BigDecimal.ZERO);
            }

            stats.add(stat);
        }

        redisTemplate.opsForValue().set(cacheKey, stats, 30, TimeUnit.MINUTES);
        return stats;
    }
}
