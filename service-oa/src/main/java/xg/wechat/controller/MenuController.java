package xg.wechat.controller;


import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import xg.common.result.Result;
import xg.model.wechat.Menu;
import xg.vo.wechat.MenuVo;
import xg.wechat.service.MenuService;

import java.util.List;

/**
 * <p>
 * 菜单 前端控制器
 * </p>
 *
 * @author xg
 * @since 2023-04-11
 */
@RestController
@RequestMapping("admin/wechat/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;


    //@PreAuthorize("hasAuthority('bnt.menu.syncMenu')")
    @ApiOperation(value = "同步菜单")
    @GetMapping("syncMenu")
    public Result createMenu() {
        menuService.syncMenu();
        return Result.ok();
    }



    //@PreAuthorize("hasAuthority('bnt.menu.list')")
    @ApiOperation(value = "获取全部菜单")
    @GetMapping("findMenuInfo")
    public Result findMenuInfo() {
        List<MenuVo> menuList = menuService.findMenuInfo();
        return Result.ok(menuList);
    }


//    @PreAuthorize("hasAuthority('bnt.menu.removeMenu')")
    @ApiOperation(value = "删除菜单")
    @DeleteMapping("removeMenu")
    public Result removeMenu() {
        menuService.removeMenu();
        return Result.ok();
    }



}

