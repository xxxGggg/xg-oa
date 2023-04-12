package xg.auth.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xg.model.system.SysMenu;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author xg
 * @since 2023-04-07
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {


    List<SysMenu> findMenuListByUserId(@Param("userId") Long userId);
}
