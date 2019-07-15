package com.audit.materialaudit.service.impl;

import com.audit.materialaudit.mapper.VPSItemMapper;
import com.audit.materialaudit.model.VPSItem;
import com.audit.materialaudit.model.VPSItemExample;
import com.audit.materialaudit.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    @Autowired
    private VPSItemMapper vpsItemMapper;

    @Override
    public List<VPSItem> queryItems() {
        VPSItemExample example = new VPSItemExample();
        example.createCriteria().andStatusEqualTo(1);
        return vpsItemMapper.selectByExample(example);
    }
}
