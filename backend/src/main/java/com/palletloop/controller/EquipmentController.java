package com.palletloop.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.palletloop.common.Result;
import com.palletloop.entity.Equipment;
import com.palletloop.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping("/page")
    public Result<IPage<Equipment>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long typeId) {
        return Result.success(equipmentService.page(current, size, typeId));
    }

    @GetMapping("/{id}")
    public Result<Equipment> getById(@PathVariable Long id) {
        return Result.success(equipmentService.getById(id));
    }

    @PostMapping
    public Result<?> save(@RequestBody @Validated Equipment entity) {
        return equipmentService.save(entity) ? Result.success() : Result.error("保存失败");
    }

    @PutMapping
    public Result<?> update(@RequestBody @Validated Equipment entity) {
        return equipmentService.updateById(entity) ? Result.success() : Result.error("更新失败");
    }
}
