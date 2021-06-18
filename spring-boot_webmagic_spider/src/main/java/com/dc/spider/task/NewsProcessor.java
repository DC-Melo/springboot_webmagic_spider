package com.dc.spider.task;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import javax.swing.text.Document;

import javax.xml.bind.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jsoup.select.Elements;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;

import com.dc.spider.pojo.NewsInfo;

@Component
public class NewsProcessor implements PageProcessor {
    private static final Log log = LogFactory.getLog(NewsProcessor.class);


    @Override
    public void process(Page page) {
        log.info("处理页面:");
        //保存html
        try {
            Date date = new Date();	//创建一个date对象
            DateFormat format=new SimpleDateFormat("yyyyMMdd_HHMMSS"); //定义格式
            String name="按照秒搜索"+format.format(date)+".html";
            FileOutputStream fos = new FileOutputStream("web/"+name,false);
            //true表示在文件末尾追加
            fos.write(page.getHtml().toString().getBytes());
            fos.close();
        } catch(Exception e){
            e.printStackTrace();
        }

        if (page.getUrl().toString().contains("www.baidu.com")){
            log.info("百度新闻列表:"+page.getUrl().toString());
            this.saveItemNewsInfo(page);
        }else{
            log.info("新闻:"+page.getUrl().toString());
            this.saveNewsInfo(page);
        }

    }
    private void saveItemNewsInfo(Page page) {
        log.info("百度列表解析：");
        ArrayList<NewsInfo> newsInfoList = new ArrayList<NewsInfo>();
        Elements titles = page.getHtml().getDocument().select("h3");     //获得非百度百科块
        Elements publishers = page.getHtml().getDocument().getElementsByClass("c-color-gray c-font-normal c-gap-right");
        Elements contents = page.getHtml().getDocument().getElementsByClass("c-font-normal c-color-text");
        Elements times = page.getHtml().getDocument().getElementsByClass("c-color-gray2 c-font-normal");
        for (int i = 0;i < titles.size();i++){
            NewsInfo newsInfo=new NewsInfo();
            log.info(titles.get(i).select("a").attr("href"));
            log.info(titles.get(i).text());
            log.info(contents.get(i).text());
            newsInfo.setUrl(titles.get(i).select("a").attr("href"));
            newsInfo.setTitle(titles.get(i).text());
            newsInfo.setSummary(contents.get(i).text());
            // newsInfo.setContent(contents.get(i).text());
            try {
                newsInfo.setPublisher(publishers.get(i).text());
                newsInfo.setPublishTime(times.get(i).text());
            }catch(Exception   ex){
                log.info("没有找到出版者或时间");
            }
            newsInfoList.add(newsInfo);
        }
        page.putField("newsInfoList",newsInfoList);
        page.putField("newsType","news");
    }
    private void saveNewsInfo(Page page) {
        //创建招聘详情对象
        ArrayList<NewsInfo> newsInfoList = new ArrayList<NewsInfo>();
        NewsInfo newsInfo=new NewsInfo();
        // 获取数据，封装到对象中
        newsInfo.setUrl(page.getUrl().toString());

        // SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date(); // this object contains the current date value
        newsInfo.setPublishTime(formatter.format(date));

        //把结果保存起来
        newsInfoList.add(newsInfo);
        page.putField("newsInfoList",newsInfoList);
        page.putField("newsType","news");
    }

    private Site site=Site.me()
            // .setCharset("gbk")
            // .header("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.80 Safari/537.36")
    		.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
            .setCharset("utf-8")
            .setTimeOut(10*1000)
            .setRetrySleepTime(3000) //设置重试的间隔时间
            .setRetryTimes(3);       //设置重试的次数


    @Override
    public Site getSite() {
        return site;
    }

    @Autowired
    private SpringDataPipeline springDataPipeline;


    //initialDelay当任务启动后，等多久执行方法
    @Scheduled(initialDelay = 1000,fixedDelay = 20*1000)
    public void process(){
        log.info("新一轮按秒抓取");
        Spider.create(new NewsProcessor())
                .addUrl("https://www.baidu.com/s?rtt=1&bsst=1&cl=2&tn=news&ie=utf-8&word=%E6%AF%94%E7%89%B9%E5%B8%81+%E6%9A%B4%E6%B6%A8") //比特币暴涨
                .addUrl("https://www.baidu.com/s?rtt=1&bsst=1&cl=2&tn=news&ie=utf-8&word=%E6%AF%94%E7%89%B9%E5%B8%81+%E6%9A%B4%E8%B7%8C") //比特币暴跌
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))
                .thread(10)
                .addPipeline(this.springDataPipeline)
                .run();
    }
}
