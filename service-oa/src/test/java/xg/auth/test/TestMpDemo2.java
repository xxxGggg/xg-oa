package xg.auth.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xg.auth.service.SysRoleService;
import xg.model.system.SysRole;

import java.util.List;

/**
 * @author XG
 * @create 2023-04-06 16:48
 */
@SpringBootTest
public class TestMpDemo2 {
    @Autowired
    private SysRoleService sysRoleService;

    @Test
    public void getAll() {
        List<SysRole> list = sysRoleService.list();
        list.forEach(System.out::println);
    }
}
