package xg.wechat.service;


import com.baomidou.mybatisplus.extension.service.IService;
import xg.model.wechat.Menu;
import xg.vo.wechat.MenuVo;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author xg
 * @since 2023-04-11
 */
public interface MenuService extends IService<Menu> {

    List<MenuVo> findMenuInfo();

    void syncMenu();

    void removeMenu();
}
