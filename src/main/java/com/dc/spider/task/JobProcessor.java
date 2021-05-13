package com.dc.spider.task;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;

@Component
public class JobProcessor implements PageProcessor {

    private String url="https://www.baidu.com/s?rtt=1&bsst=1&cl=2&tn=news&ie=utf-8&word=%E6%AF%94%E7%89%B9%E5%B8%81&ie=utf-8&ie=utf-8";

    @Override
    public void process(Page page) {
        String html = page.getHtml().toString();
        System.out.println(123);
    }

    private Site site=Site.me()
            // .setCharset("gbk")
            .setCharset("utf8")
            .setTimeOut(10*1000)
            .setRetrySleepTime(3000) //设置重试的间隔时间
            .setRetryTimes(3);       //设置重试的次数

    @Override
    public Site getSite() {
        return site;
    }

    // @Autowired
    // private SpringDataPipeline springDataPipeline;


    //initialDelay当任务启动后，等多久执行方法
    @Scheduled(initialDelay = 1000,fixedDelay = 1*1000)
    public void process(){
        System.out.println(String.format("新的一轮当前时间：%s", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        Spider.create(new JobProcessor())
                .addUrl(url)
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))
                .thread(10)
                // .addPipeline(this.springDataPipeline)
                .run();
    }
}
