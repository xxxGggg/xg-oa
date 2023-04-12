package xg.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;

import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import xg.common.result.Result;
import xg.process.service.OaProcessService;
import xg.vo.process.ProcessQueryVo;
import xg.vo.process.ProcessVo;


/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author xg
 * @since 2023-04-10
 */
@RestController
@RequestMapping("/admin/process")
public class OaProcessController {
    @Autowired
    private OaProcessService processService;

    //@PreAuthorize("hasAuthority('bnt.process.list')")
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(
            @PathVariable Long page,
            @PathVariable Long limit,
            ProcessQueryVo processQueryVo) {
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        IPage<ProcessVo> pageModel = processService.selectPage(pageParam, processQueryVo);
        return Result.ok(pageModel);
    }





}

