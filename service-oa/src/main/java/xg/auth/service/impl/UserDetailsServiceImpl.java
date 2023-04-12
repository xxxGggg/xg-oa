package xg.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import xg.auth.service.SysMenuService;
import xg.security.custom.CustomUser;
import xg.security.custom.UserDetailsService;
import xg.auth.service.SysUserService;
import xg.model.system.SysUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author XG
 * @create 2023-04-09 15:46
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getByUsername(username);
        if(null == sysUser) {
            throw new UsernameNotFoundException("用户名不存在！");
        }

        if(sysUser.getStatus().intValue() == 0) {
            throw new RuntimeException("账号已停用");
        }
        List<String> userPermsList = sysMenuService.findUserPermsByUserId(sysUser.getId());
        List<SimpleGrantedAuthority> authList = new ArrayList<>();
        for(String perms : userPermsList) {
            authList.add(new SimpleGrantedAuthority(perms.trim()));
        }
        return new CustomUser(sysUser, authList);
    }
}
