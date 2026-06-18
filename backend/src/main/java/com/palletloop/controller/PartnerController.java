package com.palletloop.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.palletloop.common.Result;
import com.palletloop.entity.Partner;
import com.palletloop.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partners")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;

    @GetMapping("/page")
    public Result<IPage<Partner>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type) {
        return Result.success(partnerService.page(current, size, name, type));
    }

    @GetMapping("/list")
    public Result<List<Partner>> list() {
        return Result.success(partnerService.list());
    }

    @GetMapping("/{id}")
    public Result<Partner> getById(@PathVariable Long id) {
        return Result.success(partnerService.getById(id));
    }

    @PostMapping
    public Result<?> save(@RequestBody @Validated Partner entity) {
        return partnerService.save(entity) ? Result.success() : Result.error("保存失败");
    }

    @PutMapping
    public Result<?> update(@RequestBody @Validated Partner entity) {
        return partnerService.updateById(entity) ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return partnerService.removeById(id) ? Result.success() : Result.error("删除失败");
    }
}
