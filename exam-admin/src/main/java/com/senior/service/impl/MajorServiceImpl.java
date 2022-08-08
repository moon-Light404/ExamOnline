package com.senior.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senior.entity.Major;
import com.senior.entity.UserRole;
import com.senior.mapper.MajorMapper;
import com.senior.mapper.UserRoleMapper;
import com.senior.service.MajorService;
import com.senior.service.UserRoleService;
import org.springframework.stereotype.Service;

@Service
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements MajorService {
}
