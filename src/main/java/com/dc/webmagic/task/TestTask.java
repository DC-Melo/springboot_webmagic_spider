package com.dc.webmagic.task;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



@Component
public class TestTask {

    /**
     * @title 定时打印
     * @desc  每隔两秒钟打印一次当前时间
     * @param :
     * @return
     * @author <a href="mailto:avaos.wei@gmail.com">avaos.wei</a>
     * @date 2020-03-25 13:32
     */
    @Scheduled(fixedRate = 2000)
    public void output() {
        System.out.println(String.format("当前时间：%s", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
    }

}
