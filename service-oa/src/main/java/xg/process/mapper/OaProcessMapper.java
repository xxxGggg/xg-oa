package xg.process.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import xg.model.process.Process;
import xg.vo.process.ProcessQueryVo;
import xg.vo.process.ProcessVo;


/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author xg
 * @since 2023-04-10
 */
public interface OaProcessMapper extends BaseMapper<Process> {
    IPage<ProcessVo> selectPage(Page<ProcessVo> page, @Param("vo") ProcessQueryVo processQueryVo);
}
