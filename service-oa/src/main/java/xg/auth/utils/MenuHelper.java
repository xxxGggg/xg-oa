package xg.auth.utils;

import xg.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XG
 * @create 2023-04-07 20:34
 */
public class MenuHelper {

    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {
        List<SysMenu> trees = new ArrayList<>();
        for(SysMenu sysMenu : sysMenuList) {
            if(sysMenu.getParentId() == 0) {
                trees.add(getChildren(sysMenu,sysMenuList));
            }
        }

        return trees;
    }

    public static SysMenu getChildren(SysMenu sysMenu,List<SysMenu> sysMenuList) {
        sysMenu.setChildren(new ArrayList<>());
        for(SysMenu it : sysMenuList) {
            if(sysMenu.getId().longValue() == it.getParentId().longValue()){
                if(sysMenu.getChildren() == null) {
                    sysMenu.setChildren(new ArrayList<>());
                }
                sysMenu.getChildren().add(getChildren(it,sysMenuList));
            }
        }
        return sysMenu;
    }
}
