package xg.process.service;


import com.baomidou.mybatisplus.extension.service.IService;
import xg.model.process.ProcessRecord;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author xg
 * @since 2023-04-11
 */
public interface OaProcessRecordService extends IService<ProcessRecord> {
    void record(Long processId,Integer status,String description);
}
