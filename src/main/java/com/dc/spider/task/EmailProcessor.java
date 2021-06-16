package com.dc.spider.task;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
// @Service
@Component
public class EmailProcessor {
    // @Scheduled(cron = "0 * * * * ?")
    // @Scheduled(initialDelay = 1000,fixedDelay = 20*1000)
    // @Scheduled(cron = "0 0 18 * * ?")
    @Scheduled(cron = "0 0 18 ? * FRI")
    public void sendNewsEmail(){
        System.out.println("定时任务1:"+new Date()+"hello");
        Date date = new Date();	//创建一个date对象
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd"); //定义格式
        String title="news-"+format.format(date);
        File file = new File("email_news.md");// 读取
        if(file.isFile()){ // 判断是否是文件夹
            String emailCmd = "mutt -s \""+title+"\" melodachor@gmail.com -c 627469914@qq.com -b 365553100@qq.com < email_info.md";
            try {
                // String[] cmd = new String[3];
                Runtime rt = Runtime.getRuntime();
                System.out.println("Execing" +emailCmd );
                //Change here: execute a shell with the command line instead of echo:
                Process proc = rt.exec(new String[]{"/bin/sh","-c", emailCmd});
                // any error message?
                StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(),"ERROR");
                // any output?
                StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(),"OUTPUT");
                // kick them off
                errorGobbler.start();
                outputGobbler.start();
                // any error???
                int exitVal = proc.waitFor();
                System.out.println("ExitValue:" + exitVal);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            file.delete();// 删除
        }
    }
    // @Scheduled(cron = "1 * * * * ?")
    // @Scheduled(initialDelay = 1000,fixedDelay = 20*1000)
    // @Scheduled(cron = "0 0 8 * * ?")
    @Scheduled(cron = "0 0 8 ? * SAT")
    public void sendInfoEmail(){
        System.out.println("定时任务2:"+new Date()+"No++");
        Date date = new Date();	//创建一个date对象
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd"); //定义格式
        String title="Info-"+format.format(date);
        File file = new File("email_info.md");// 读取
        if(file.isFile()){ // 判断是否是文件夹
            String cmd = "mutt -s \""+title+"\" melodachor@gmail.com -c 627469914@qq.com -b 365553100@qq.com < email_info.md";
            try {
                // String[] cmd = new String[3];
                Runtime rt = Runtime.getRuntime();
                System.out.println("Execing" + cmd);
                //Change here: execute a shell with the command line instead of echo:
                Process proc = rt.exec(new String[]{"/bin/sh","-c", cmd});
                // any error message?
                StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(),"ERROR");
                // any output?
                StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(),"OUTPUT");
                // kick them off
                errorGobbler.start();
                outputGobbler.start();
                // any error???
                int exitVal = proc.waitFor();
                System.out.println("ExitValue:" + exitVal);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            file.delete();// 删除
        }
    }

    static class StreamGobbler extends Thread {

        InputStream is;
        String type;

        StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    System.out.println(type +">" + line);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
