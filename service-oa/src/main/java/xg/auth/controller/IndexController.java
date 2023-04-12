package xg.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xg.auth.service.SysMenuService;
import xg.auth.service.SysUserService;
import xg.common.config.exception.XgException;
import xg.common.jwt.JwtHelper;
import xg.common.result.Result;
import xg.common.utils.MD5;
import xg.model.system.SysMenu;
import xg.model.system.SysUser;
import xg.vo.system.LoginVo;
import xg.vo.system.RouterVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XG
 * @create 2023-04-07 10:03
 */
@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;

    @ApiOperation("登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) {
//        Map<String,Object> map = new HashMap<>();
//        map.put("token","admin-token");
//        return Result.ok(map);
        //获取用户名和密码
        String username = loginVo.getUsername();

//        String password = loginVo.getPassword();
        //查询数据库
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername,username);
        SysUser sysUser = sysUserService.getOne(wrapper);

        //用户信息是否存在
        if(sysUser == null) {
            throw new XgException(201,"用户不存在");
        }

        //判断密码
        String passwordDB = sysUser.getPassword();
        String passwordInput = MD5.encrypt(loginVo.getPassword());
        if(!passwordDB.equals(passwordInput)) {
            throw new XgException(201,"密码错误");
        }
        //是否被禁用
        if(sysUser.getStatus() == 0) {
            throw new XgException(201,"用户被禁用");
        }
        //生成token字符串
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());

        Map<String,Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }

    @GetMapping("info")
    public Result info(HttpServletRequest request) {

//        Enumeration<String> headerNames = request.getHeaderNames();
//        while(headerNames.hasMoreElements()) {//判断是否还有下一个元素
//            String nextElement = headerNames.nextElement();//获取headerNames集合中的请求头
//
//            String header2 = request.getHeader(nextElement);//通过请求头得到请求内容
//
//            System.err.println("请求头==========key" + nextElement + "----------VALUE:" + header2);}
        //step1 获取token
        String token = request.getHeader("token");
        System.out.println(token);
        //step2 从token中获取id，username
        Long userId = JwtHelper.getUserId(token);
        //step3 根据id查询用户信息
        SysUser sysUser = sysUserService.getById(userId);
        //step4 获取用户可以操作的菜单和按钮
        List<RouterVo> routerList = sysMenuService.findUserMenuListByUserId(userId);
        List<String> permsList = sysMenuService.findUserPermsByUserId(userId);

        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name",sysUser.getName());
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        map.put("routers",routerList);
        map.put("buttons",permsList);
        return Result.ok(map);
    }

    @PostMapping("logout")
    public Result logout() {
        return Result.ok();
    }
}
