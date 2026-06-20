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
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Override
    public void exportPartnerStats(Long partnerId, HttpServletResponse response) {
        List<StatisticsDTO.PartnerStat> stats;
        if (partnerId != null) {
            stats = getPartnerStats(partnerId);
        } else {
            stats = getAllPartnerStats();
        }

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            Sheet sheet = workbook.createSheet("合作方器具统计");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            String[] headers = {"合作方名称", "器具类型", "累计发出", "累计回收", "损坏数量", "在外结存", "周转次数", "损耗率(%)"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            int totalDispatched = 0;
            int totalReturned = 0;
            int totalDamaged = 0;
            int totalOutstanding = 0;
            int totalTurnover = 0;

            for (StatisticsDTO.PartnerStat stat : stats) {
                Row row = sheet.createRow(rowNum++);

                createCell(row, 0, stat.getPartnerName() != null ? stat.getPartnerName() : "", dataStyle);
                createCell(row, 1, stat.getTypeName() != null ? stat.getTypeName() : "", dataStyle);
                createCell(row, 2, stat.getDispatched() != null ? stat.getDispatched() : 0, dataStyle);
                createCell(row, 3, stat.getReturned() != null ? stat.getReturned() : 0, dataStyle);
                createCell(row, 4, stat.getDamaged() != null ? stat.getDamaged() : 0, dataStyle);
                createCell(row, 5, stat.getOutstanding() != null ? stat.getOutstanding() : 0, dataStyle);
                createCell(row, 6, stat.getTurnoverCount() != null ? stat.getTurnoverCount() : 0, dataStyle);
                createCell(row, 7, stat.getLossRate() != null ? stat.getLossRate().setScale(2, RoundingMode.HALF_UP).toString() : "0.00", dataStyle);

                totalDispatched += stat.getDispatched() != null ? stat.getDispatched() : 0;
                totalReturned += stat.getReturned() != null ? stat.getReturned() : 0;
                totalDamaged += stat.getDamaged() != null ? stat.getDamaged() : 0;
                totalOutstanding += stat.getOutstanding() != null ? stat.getOutstanding() : 0;
                totalTurnover += stat.getTurnoverCount() != null ? stat.getTurnoverCount() : 0;
            }

            Row totalRow = sheet.createRow(rowNum);
            createCell(totalRow, 0, "合计", headerStyle);
            createCell(totalRow, 1, "", headerStyle);
            createCell(totalRow, 2, totalDispatched, headerStyle);
            createCell(totalRow, 3, totalReturned, headerStyle);
            createCell(totalRow, 4, totalDamaged, headerStyle);
            createCell(totalRow, 5, totalOutstanding, headerStyle);
            createCell(totalRow, 6, totalTurnover, headerStyle);
            createCell(totalRow, 7, "", headerStyle);

            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 18 * 256);
            }

            String fileName = "合作方器具统计_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + "\"");
            response.setCharacterEncoding("UTF-8");

            try (OutputStream out = response.getOutputStream()) {
                workbook.write(out);
                out.flush();
            }
            workbook.dispose();
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }
        cell.setCellStyle(style);
    }
}
