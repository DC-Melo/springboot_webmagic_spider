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
public class PBCNewsProcessor implements PageProcessor {
    private static final Log log = LogFactory.getLog(PBCNewsProcessor.class);


    @Override
    public void process(Page page) {
        log.info("处理页面:");
        //保存html
        try {
            Date date = new Date();	//创建一个date对象
            DateFormat format=new SimpleDateFormat("yyyyMMdd_HHMMSS"); //定义格式
            String name="文件时间"+format.format(date)+".html";
            FileOutputStream fos = new FileOutputStream("web/"+name,false);
            //true表示在文件末尾追加
            fos.write(page.getHtml().toString().getBytes());
            fos.close();
        } catch(Exception e){
            e.printStackTrace();
        }

        if (page.getHtml().toString().contains("请开启JavaScript并刷新该页")){
            String redirect_url=get_redirect_url(page);
            if (!redirect_url.equals("")) page.addTargetRequest(redirect_url);
            log.info("页面需重定向:"+redirect_url);
        }else if (page.getUrl().toString().contains("www.baidu.com")){
            log.info("百度新闻列表:"+page.getUrl().toString());
            this.saveItemNewsInfo(page);
        }else if(page.getUrl().toString().contains("www.pbc.gov.cn")){
            if(page.getUrl().toString().contains("www.pbc.gov.cn/goutongjiaoliu/113456/113469/11040/")){
                log.info("央行新闻:"+page.getUrl().toString());
                this.savePbcNewsInfo(page);
            }else{
                log.info("央行新闻列表:"+page.getUrl().toString());
            }
        }else{
            log.info("新闻:"+page.getUrl().toString());
            this.saveNewsInfo(page);
        }

    }
    public String get_redirect_url(Page page){
        String redirect_url="";
        Elements elScripts = page.getHtml().getDocument().getElementsByTag("script");
        String js_code = elScripts.get(0).data().toString();
        // System.out.printf(js_code);
        js_code = js_code.replaceAll("atob\\(","window\\[\"atob\"\\]\\(");
        String js_fn= "function getURL(){ var window = {};" + js_code + "return window[\"location\"];}";
        // System.out.printf(js_fn);   
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        try{
            engine.eval(js_fn);
            if (engine instanceof Invocable) {
                Invocable in = (Invocable) engine;
                redirect_url= in.invokeFunction("getURL").toString();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return redirect_url;
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
            newsInfo.setPublisher(publishers.get(i).text());
            newsInfo.setContent(contents.get(i).text());
            newsInfo.setPublishTime(times.get(i).text());
            newsInfoList.add(newsInfo);
        }
        page.putField("newsInfoList",newsInfoList);
        page.putField("newsType","pbc");
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
        page.putField("newsType","pbc");
    }
    private void savePbcNewsInfo(Page page) {
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
    @Scheduled(initialDelay = 1000,fixedDelay = 3600*1000)
    public void process(){
        log.info("新一轮央行按秒抓取");
        Spider.create(new PBCNewsProcessor())
                .addUrl("http://www.pbc.gov.cn/goutongjiaoliu/113456/113469/11040/index1.html")
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))
                .thread(10)
                .addPipeline(this.springDataPipeline)
                .run();
    }
}
