package xg.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import xg.model.system.SysUser;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author xg
 * @since 2023-04-07
 */
public interface SysUserService extends IService<SysUser> {

    void updateStatus(Long id, Integer status);

    SysUser getByUsername(String username);

    Map<String, Object> getCurrentUser();
}
