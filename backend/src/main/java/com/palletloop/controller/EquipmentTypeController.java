package com.palletloop.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.palletloop.common.Result;
import com.palletloop.entity.EquipmentType;
import com.palletloop.service.EquipmentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment-types")
@RequiredArgsConstructor
public class EquipmentTypeController {

    private final EquipmentTypeService equipmentTypeService;

    @GetMapping("/page")
    public Result<IPage<EquipmentType>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name) {
        return Result.success(equipmentTypeService.page(current, size, name));
    }

    @GetMapping("/list")
    public Result<List<EquipmentType>> list() {
        return Result.success(equipmentTypeService.list());
    }

    @GetMapping("/{id}")
    public Result<EquipmentType> getById(@PathVariable Long id) {
        return Result.success(equipmentTypeService.getById(id));
    }

    @PostMapping
    public Result<?> save(@RequestBody @Validated EquipmentType entity) {
        return equipmentTypeService.save(entity) ? Result.success() : Result.error("保存失败");
    }

    @PutMapping
    public Result<?> update(@RequestBody @Validated EquipmentType entity) {
        return equipmentTypeService.updateById(entity) ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return equipmentTypeService.removeById(id) ? Result.success() : Result.error("删除失败");
    }
}
