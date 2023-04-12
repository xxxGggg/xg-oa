package xg.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xg.auth.service.SysRoleService;
import xg.auth.service.SysUserRoleService;
import xg.common.config.exception.XgException;
import xg.common.result.Result;
import xg.model.system.SysRole;
import xg.vo.system.AssginRoleVo;
import xg.vo.system.SysRoleQueryVo;

import java.util.List;
import java.util.Map;
//http://localhost:8800/admin/system/sysRole/findAll
/**
 * @author XG
 * @create 2023-04-06 18:06
 */
@RestController
@RequestMapping("/admin/system/sysRole")
@Api(tags = "角色管理接口")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;



    @ApiOperation("获取角色")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId) {
        Map<String,Object> map = sysRoleService.findRoleDataByUserId(userId);
        return Result.ok(map);
    }

    @ApiOperation("为用户分配角色")
    @PostMapping("doAssign")
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo) {
        sysRoleService.doAssign(assginRoleVo);
        return Result.ok();
    }


//    @GetMapping("/findAll")
//    public List<SysRole> findAll() {
//        return sysRoleService.list();
//    }


    //http://localhost:8800/doc.html
    @ApiOperation("查询所有角色")
    @GetMapping("/findAll")
    public Result findAll() {

//        try {
//            int i = 1 / 0;
//        }catch (Exception e) {
//            throw new XgException(20001,"执行了自定义异常");
//        }

        List<SysRole> list = sysRoleService.list();
        return Result.ok(list);
    }


    //page:当前页，limit每页显示记录数
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("条件分页查询")
    @GetMapping("/{page}/{limit}")
    public Result pageQueryRole(@PathVariable Long page,
                                @PathVariable Long limit,
                                SysRoleQueryVo sysRoleQueryVo) {
        Page<SysRole> rolePage = new Page<>(page,limit);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if(!StringUtils.isEmpty(roleName)) {
            wrapper.like(SysRole::getRoleName,roleName);
        }

        Page<SysRole> pageModel = sysRoleService.page(rolePage, wrapper);
        return Result.ok(rolePage);
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result save(@RequestBody SysRole role) {
        boolean isSuccess = sysRoleService.save(role);
        if(isSuccess) return Result.ok();
        else return Result.fail();
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("根据id查询")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        return Result.ok(sysRole);
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody SysRole role) {
        boolean isSuccess = sysRoleService.updateById(role);
        if(isSuccess) return Result.ok();
        else return Result.fail();
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("根据id删除角色")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean isSuccess = sysRoleService.removeById(id);
        if(isSuccess) return Result.ok();
        else return Result.fail();
    }


    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("批量删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids) {
        boolean isSuccess = sysRoleService.removeByIds(ids);
        if(isSuccess) return Result.ok();
        else return Result.fail();
    }



}
