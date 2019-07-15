package com.audit.materialaudit.schedule;

import com.audit.materialaudit.constant.constConfig;
import com.audit.materialaudit.mapper.VpsMapper;
import com.audit.materialaudit.model.Vps;
import com.audit.materialaudit.model.VpsExample;
import com.audit.materialaudit.utils.DataTransferUtils;
import com.audit.materialaudit.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UpdateVPSTask {


    @Autowired
    private VpsMapper vpsMapper;

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void work() throws IOException {
        String jsonString = FileUtils.getFileContents(constConfig.VPSCONFIGPATH);
        Map<String,Object> configMap = DataTransferUtils.jsonToMap(jsonString);
        //获取配置文件port password映射
        Map<String,String> configPortPassword = (Map<String, String>) configMap.get("port_password");
        log.debug("config"+configPortPassword);
        //获取数据库vps信息
        VpsExample vpsExample = new VpsExample();
        vpsExample.createCriteria().andDeleteFlagEqualTo((byte)0);
        List<Vps> vpsList = vpsMapper.selectByExample(vpsExample);
        log.debug("vpsList:"+vpsList);
        if(!CollectionUtils.isEmpty(vpsList)){
            Map<String,String> dbPortPassword = new HashMap<>();
            //数据映射
            vpsList.forEach(vps->dbPortPassword.put(vps.getPort().toString(),vps.getPassword()));
            log.debug("dbportPassword:"+dbPortPassword);
            //将配置文件port转成list
            List<Integer> configPortList = new ArrayList<>();// = Arrays.asList(portPassword.keySet().toArray());
            configPortPassword.forEach((key, value) -> configPortList.add(Integer.valueOf(key)));
            log.debug("configPortList:"+configPortList);
            //将数据库port转成list
            List<Integer> dbPortList = new ArrayList<>();
            dbPortPassword.forEach((key,value)->dbPortList.add(Integer.valueOf(key)));
            log.debug("dbPortList:"+dbPortList);
            //获取数据库中于配置文件不存在的端口信息并插入vps表
            for(Integer port:configPortList){
                if(!dbPortPassword.containsKey(port.toString())){
                    VpsExample vpsExample1 = new VpsExample();
                    vpsExample1.createCriteria().andPortEqualTo(port).andDeleteFlagEqualTo((byte)1);
                    List<Vps> deletedVpsList = vpsMapper.selectByExample(vpsExample1);
                    if(!CollectionUtils.isEmpty(deletedVpsList)){
                        Vps vps = new Vps();
                        vps.setDeleteFlag((byte)0);
                        vps.setUseStatus((byte)1);
                        vps.setPassword(configPortPassword.get(port.toString()));
                        VpsExample updateVPSExample = new VpsExample();
                        updateVPSExample.createCriteria().andIdEqualTo(deletedVpsList.get(0).getId());
                        vpsMapper.updateByExampleSelective(vps,updateVPSExample);
                    }else{
                        String password = configPortPassword.get(port.toString());
                        Vps insertVPS = new Vps();
                        insertVPS.setPort(port);
                        insertVPS.setDefault1((byte)1);
                        insertVPS.setPassword(password);
                        insertVPS.setIp(configMap.get("server").toString());
                        insertVPS.setUseStatus((byte)1);
                        insertVPS.setDeleteFlag((byte)0);
                        insertVPS.setCreateTime(System.currentTimeMillis());
                        insertVPS.setUpdateTime(System.currentTimeMillis());
                        vpsMapper.insert(insertVPS);
                    }

                }
            }
            //获取配置信息中已删除的数据并将vps表中的数据置为已删除
            for(Integer port:dbPortList){
                if(!configPortPassword.containsKey(port.toString())){
                    VpsExample removeExample = new VpsExample();
                    removeExample.createCriteria().andDeleteFlagEqualTo((byte)0).andPortEqualTo(port);
                    Vps res = vpsMapper.selectByExample(removeExample).get(0);
                    VpsExample reExample = new VpsExample();
                    reExample.createCriteria().andIdEqualTo(res.getId());
                    Vps vps = new Vps();
                    vps.setDeleteFlag((byte)1);
                    vpsMapper.updateByExampleSelective(vps,reExample);
                }
            }
        }else{
            if(!CollectionUtils.isEmpty(configPortPassword)){
                for(Map.Entry<String,String> entry:configPortPassword.entrySet()){
                    Vps vps = new Vps();
                    vps.setPassword(entry.getValue());
                    vps.setPort(Integer.valueOf(entry.getKey()));
                    vps.setIp(configMap.get("server").toString());
                    vps.setDefault1((byte)1);
                    vps.setUseStatus((byte)1);
                    vps.setDeleteFlag((byte)0);
                    vps.setCreateTime(System.currentTimeMillis());
                    vps.setUpdateTime(System.currentTimeMillis());
                    try {
                        vpsMapper.insert(vps);
                    }catch (Exception e){
                        e.printStackTrace();
                        log.error("定时更新vpsrecord表任务vps信息插入数据库失败");
                    }
                }
            }
        }
    }
}
