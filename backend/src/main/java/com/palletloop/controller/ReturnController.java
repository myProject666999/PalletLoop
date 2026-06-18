package com.palletloop.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.palletloop.common.Result;
import com.palletloop.dto.ReturnDTO;
import com.palletloop.entity.ReturnRecord;
import com.palletloop.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    @GetMapping("/page")
    public Result<IPage<ReturnRecord>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long partnerId,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) String returnNo) {
        return Result.success(returnService.page(current, size, partnerId, typeId, returnNo));
    }

    @GetMapping("/{id}")
    public Result<ReturnRecord> getById(@PathVariable Long id) {
        return Result.success(returnService.getById(id));
    }

    @PostMapping
    public Result<?> returnEquipment(@RequestBody @Validated ReturnDTO dto) {
        return returnService.returnEquipment(dto);
    }
}
