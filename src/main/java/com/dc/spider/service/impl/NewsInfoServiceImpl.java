package com.dc.spider.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    private static final Log log = LogFactory.getLog(NewsInfoServiceImpl.class);

    @Autowired
    private NewsInfoDao newsInfoDao;


    @Override
    @Transactional
    public void save(NewsInfo newsInfo,String newsType) {
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
            if(! (newsType.contains("news") && newsInfo.getPublishTime().contains("天"))){
                log.info("+++++++++++++++++++++++++++");
                this.send2DingDing(newsInfo,newsType);
            }else{
                log.info("---------------------------");
            }
        }

    }

    @Override
    public void saveList(List<NewsInfo> newsInfoList,String newsType) {
        //根据url和发布时间查询数据
        for(int i=0;i<newsInfoList.size();i++){
                this.save(newsInfoList.get(i),newsType);
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

    public void send2DingDing(NewsInfo newsInfo,String newsType){
        // 请求的JSON数据，这里我用map在工具类里转成json格式
        Map<String,Object> json=new HashMap();
        json.put("msgtype","text");
        Map<String,Object> text=new HashMap();
        String content =newsInfo.getTitle()+"\n"+newsInfo.getPublishTime()+"\n"+newsInfo.getSummary()+"\n"+newsInfo.getUrl()+"\n[DC]";
        text.put("content",content);
        json.put("text",text);

        // 钉钉的webhook
        String dingDingToken;
        if (newsType.contains("news")){
            dingDingToken="https://oapi.dingtalk.com/robot/send?access_token=f489f31bfbe63eae49aa3bd7ed4150d875da5179586a9fab830b4150540223cc";
            writeFileAppend("email_news.md","## "+newsInfo.getTitle());
            writeFileAppend("email_news.md","-------");
            writeFileAppend("email_news.md",newsInfo.getUrl()+"\n"+newsInfo.getPublisher()+"\n"+newsInfo.getSummary()+"\n");
        }else if(newsType.contains("info")){
            dingDingToken="https://oapi.dingtalk.com/robot/send?access_token=960c7be25215fa627e3d46da06bbde223c36e3b43c7a706b29ca249675d0695e";
            writeFileAppend("email_info.md","## "+newsInfo.getTitle());
            writeFileAppend("email_info.md","-------");
            writeFileAppend("email_info.md",newsInfo.getUrl()+"\n"+newsInfo.getPublisher()+"\n"+newsInfo.getSummary()+"\n");
        }else{
            dingDingToken="https://oapi.dingtalk.com/robot/send?access_token=319a810ae793e42ed8f1f7447d9aef2fce5884f4c865edef3675eb1f64d49ee0";
            writeFileAppend("email_report.md","## "+newsInfo.getTitle());
            writeFileAppend("email_report.md","-------");
            writeFileAppend("email_report.md",newsInfo.getUrl()+"\n"+newsInfo.getPublisher()+"\n"+newsInfo.getSummary()+"\n");
        }
        // 发送post请求
        String response = SendHttps.sendPostByMap(dingDingToken, json);
        log.info("=====================================");
        log.info(content);
        log.info(response);
        log.info("=====================================");
    }

    public void writeFileAppend(String filePath,String text){
        //        java文件输出流
        FileOutputStream fos = null;
        //        File对象代表磁盘中实际存在的文件和目录
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                //                在文件系统中根据保存在文件中的路径信息，创建一个新的空文件。如果创建成功就会返回true，如果文件存在返回false。注意：如果他已经存在但不是文件，可能是目录也会返回false。
                boolean hasFile = file.createNewFile();
                if (!hasFile) {
                    fos = new FileOutputStream(file);
                }
            } else {
                //                以追加的方式创建一个文件输出流
                fos = new FileOutputStream(file, true);
            }
            fos.write((text+"\n").getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
