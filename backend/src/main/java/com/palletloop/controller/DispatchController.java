package com.palletloop.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.palletloop.common.Result;
import com.palletloop.dto.DispatchDTO;
import com.palletloop.entity.DispatchRecord;
import com.palletloop.service.DispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dispatches")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchService dispatchService;

    @GetMapping("/page")
    public Result<IPage<DispatchRecord>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long partnerId,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) String dispatchNo) {
        return Result.success(dispatchService.page(current, size, partnerId, typeId, dispatchNo));
    }

    @GetMapping("/{id}")
    public Result<DispatchRecord> getById(@PathVariable Long id) {
        return Result.success(dispatchService.getById(id));
    }

    @PostMapping
    public Result<?> dispatch(@RequestBody @Validated DispatchDTO dto) {
        return dispatchService.dispatch(dto);
    }
}
