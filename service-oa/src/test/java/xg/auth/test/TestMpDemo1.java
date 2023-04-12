package xg.auth.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xg.auth.mapper.SysRoleMapper;
import xg.model.system.SysRole;

import java.util.Arrays;
import java.util.List;

/**
 * @author XG
 * @create 2023-04-06 16:05
 */
@SpringBootTest
public class TestMpDemo1 {
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Test
    public void getAll() {

        List<SysRole> sysRoles = sysRoleMapper.selectList(null);
        sysRoles.forEach(System.out::println);
    }

    @Test
    public void add() {
        SysRole sysRole = new SysRole();
        sysRole.setRoleName("角色管理员");
        sysRole.setRoleCode("roleManager");
        sysRole.setDescription("3");

        sysRoleMapper.insert(sysRole);
    }

    @Test
    public void update() {
        SysRole sysRole = sysRoleMapper.selectById(9);
        sysRole.setRoleName("xg");
        sysRoleMapper.updateById(sysRole);
    }

    //logical delete
    @Test
    public void delete() {
        List<Integer> list = Arrays.asList(1, 2, 9);
        sysRoleMapper.deleteBatchIds(list);
    }

    @Test
    public void testQuery() {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("role_name","xg");
        List<SysRole> sysRoles = sysRoleMapper.selectList(wrapper);
        sysRoles.forEach(System.out::println);
    }

    @Test
    public void testLambda() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleName,"xg");
        List<SysRole> sysRoles = sysRoleMapper.selectList(wrapper);
        sysRoles.forEach(System.out::println);
    }

}
