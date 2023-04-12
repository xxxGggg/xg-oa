package xg.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xg.model.system.SysRole;
import xg.vo.system.AssginRoleVo;

import java.util.Map;

/**
 * @author XG
 * @create 2023-04-06 16:42
 */

public interface SysRoleService extends IService<SysRole> {

    Map<String, Object> findRoleDataByUserId(Long userId);

    void doAssign(AssginRoleVo assginRoleVo);
}
