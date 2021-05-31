package com.lcw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lcw.domain.FormData;
import com.lcw.mappper.FormDataMapper;
import com.lcw.service.FormDataService;
import org.springframework.stereotype.Service;

/**
 * @author ManGo
 */
@Service
public class FormDataServiceImpl extends ServiceImpl<FormDataMapper, FormData> implements FormDataService {
}
