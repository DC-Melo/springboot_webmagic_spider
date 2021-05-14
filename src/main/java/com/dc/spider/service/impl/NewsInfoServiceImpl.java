package com.dc.spider.service.impl;

import com.dc.spider.dao.NewsInfoDao;
import com.dc.spider.pojo.NewsInfo;
import com.dc.spider.service.NewsInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        }

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
}
