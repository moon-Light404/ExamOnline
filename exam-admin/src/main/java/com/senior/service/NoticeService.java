package com.senior.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.senior.entity.Notice;

public interface NoticeService extends IService<Notice> {
    // 将所有公告设置为历史公告
    boolean setAllNoticeIsHistoryNotice();
}
