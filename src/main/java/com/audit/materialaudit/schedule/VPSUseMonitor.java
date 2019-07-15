package com.audit.materialaudit.schedule;

import com.audit.materialaudit.constant.constConfig;
import com.audit.materialaudit.mapper.VpsRecordMapper;
import com.audit.materialaudit.model.VpsRecord;
import com.audit.materialaudit.model.VpsRecordExample;
import com.audit.materialaudit.utils.DataTransferUtils;
import com.audit.materialaudit.utils.FileUtils;
import com.audit.materialaudit.utils.ShellUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class VPSUseMonitor {

    @Autowired
    private VpsRecordMapper vpsRecordMapper;
    @Scheduled(cron = "0 0/30 * * * ?")
    public void work() throws IOException {
        String configPath = constConfig.VPSCONFIGPATH;
        VpsRecordExample vpsRecordExample = new VpsRecordExample();
        vpsRecordExample.createCriteria().andStatusEqualTo((byte)1).andDeleteFlagEqualTo((byte)0);
        List<VpsRecord> vpsRecordList = vpsRecordMapper.selectByExample(vpsRecordExample);
        log.error("开始执行");
        String shadowsocksConfig = null;
        Map<String,String> configPortPassword = null;
        Map<String,Object> configMap = null;
        List<String> vpsRecordIds = new ArrayList<>();
        if(!CollectionUtils.isEmpty(vpsRecordList)){
            log.error("数据不为空"+vpsRecordList);
            shadowsocksConfig = FileUtils.getFileContents(configPath);
            configMap = DataTransferUtils.jsonToMap(shadowsocksConfig);
            //获取配置文件port password映射
            configPortPassword = (Map<String, String>) configMap.get("port_password");
            Boolean flag = false;
            for(VpsRecord vpsRecord:vpsRecordList){
                Integer useTime = (Integer) vpsRecord.getUseTime();
                Long transUseTime = Long.valueOf(useTime)*86400000L;
                Long createTime = vpsRecord.getUpdateTime();
                if(System.currentTimeMillis()>(transUseTime+createTime)){
                    flag = true;
                    log.error("vps信息更新定时任务存在已过期的数据");
                    configPortPassword.remove(vpsRecord.getPort().toString());
                    vpsRecordIds.add(vpsRecord.getId().toString());
                }
            }
            Writer w = new FileWriter(configPath, false);;
            if(flag){
                //删除配置文件中已失效的port和密码
                configMap.put("port_password",configPortPassword);
                String transResult = DataTransferUtils.beanToJson(configMap);
                log.error("transResult:"+transResult.toString());
                try {
                    w.write(transResult.toString());
                }catch (Exception e){
                    log.error("vps信息更新定时任务写文件失败");
                    e.printStackTrace();
                    w.close();
                    throw e;
                }finally {
                    w.close();
                    try {
                        ShellUtils.execShell("/home/ningyunfa1/vpsproject/rebootshadowsocks.sh");
                    }catch (Exception e){
                        log.error("excute rebootshadowsocks.sh faild");
                    }
                }
                for(String id:vpsRecordIds){
                    VpsRecord vpsRecord = new VpsRecord();
                    vpsRecord.setStatus((byte)0);
                    VpsRecordExample updateExample = new VpsRecordExample();
                    updateExample.createCriteria().andIdEqualTo(Integer.valueOf(id));
                    Integer count = 0;
                    int result = 0;
                    do {
                        try {
                            result = vpsRecordMapper.updateByExampleSelective(vpsRecord,updateExample);
                        }catch (Exception e){
                            count++;
                            e.printStackTrace();
                            log.error("vps信息更新定时任务定时更新vps状态更新数据库失败");
                        }
                        if(result>0){
                            log.debug("数据更新成功");
                        }
                    }while (result<=0 && count<3);
                }
            }
        }else {
            log.error("定时更新状态未查询到数据");
        }
    }
}
