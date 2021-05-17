package com.dc.spider.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Example;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.dc.spider.dao.NewsInfoDao;
import com.dc.spider.pojo.NewsInfo;
import com.dc.spider.sendhttp.SendHttps;
import com.dc.spider.service.NewsInfoService;

@Service
public class NewsInfoServiceImpl implements NewsInfoService {

    @Autowired
    private NewsInfoDao newsInfoDao;


    @Override
    @Transactional
    public void save(NewsInfo newsInfo) {
        //根据url和发布时间查询数据
        NewsInfo param=new NewsInfo();
        param.setUrl(newsInfo.getUrl());
        // param.setTime(newsInfo.getTime());
        // param.setTime(newsInfo.getTime());

        //执行查询
        List<NewsInfo> list=this.findNewsInfo(param);

        //判断查询结果是否为空
        if(list.size()==0){
            this.newsInfoDao.saveAndFlush(newsInfo);
            if(!newsInfo.getPublishTime().contains("天")){
                this.sendDingDing(newsInfo);
            }
        }
                // this.sendDingDing(newsInfo);

    }

    @Override
    public void saveList(List<NewsInfo> newsInfoList) {
        //根据url和发布时间查询数据
        for(int i=0;i<newsInfoList.size();i++){
                this.save(newsInfoList.get(i));
        }

    }

    @Override
    public List<NewsInfo> findNewsInfo(NewsInfo newsInfo) {
        //设置查询条件
        Example example=Example.of(newsInfo);
        //执行查询
        List list=this.newsInfoDao.findAll(example);

        return list;
    }
    @Override
    public void sendDingDing(NewsInfo newsInfo){
        // 钉钉的webhook
        String dingDingToken="https://oapi.dingtalk.com/robot/send?access_token=960c7be25215fa627e3d46da06bbde223c36e3b43c7a706b29ca249675d0695e";
        // 请求的JSON数据，这里我用map在工具类里转成json格式
        Map<String,Object> json=new HashMap();
        json.put("msgtype","text");
        Map<String,Object> text=new HashMap();
        text.put("content","[dc]"+newsInfo.getTitle()+"\n"+newsInfo.getPublishTime()+"\n"+newsInfo.getContent()+"\n"+newsInfo.getUrl());
        json.put("text",text);
        // 发送post请求
        String response = SendHttps.sendPostByMap(dingDingToken, json);
    }
    @Override
    public void sendEmail(NewsInfo newsInfo){

    }
}
