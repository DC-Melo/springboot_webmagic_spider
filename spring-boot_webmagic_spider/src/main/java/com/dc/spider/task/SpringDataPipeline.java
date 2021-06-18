package com.dc.spider.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import com.dc.spider.pojo.NewsInfo;
import com.dc.spider.service.NewsInfoService;

@Component
public class SpringDataPipeline  implements Pipeline {

    @Autowired
    private NewsInfoService newsInfoService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        //获取封装好的招聘详情对象
        // NewsInfo newsInfo = resultItems.get("newsInfo");
        //判断数据是否不为空
        //if (newsInfo != null) {
        //    //如果不为空把数据保存到数据库中
        //    this.newsInfoService.save(newsInfo);
        // }
        List<NewsInfo> newsInfoList = resultItems.get("newsInfoList");
        String newsType = resultItems.get("newsType");

        if (newsInfoList != null) {
            //如果不为空把数据保存到数据库中
            this.newsInfoService.saveList(newsInfoList,newsType);
        }
    }
}
