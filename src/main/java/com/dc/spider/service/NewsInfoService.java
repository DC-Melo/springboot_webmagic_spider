package com.dc.spider.service;

import com.dc.spider.pojo.NewsInfo;

import java.util.List;

public interface NewsInfoService {

    //保存工作信息
    public void save(NewsInfo newsInfo,String newsType);

    //保存工作信息
    public void saveList(List<NewsInfo> newsInfoList,String newsType);

    // 发送钉钉
    // public void send2DingDing(NewsInfo newsInfo,String newsType);

    // 发送邮件
    // public void sendEmail(NewsInfo newsInfo);


    //根据条件查询工作信息
    public List<NewsInfo> findNewsInfo(NewsInfo newsInfo);
}
