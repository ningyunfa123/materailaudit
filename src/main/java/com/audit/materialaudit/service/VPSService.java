package com.audit.materialaudit.service;

import com.audit.materialaudit.model.request.ApplyVPSRequest;
import com.audit.materialaudit.model.response.ApplyVPSResponse;

public interface VPSService{
    ApplyVPSResponse applyVPS(ApplyVPSRequest request) throws Exception;

}
