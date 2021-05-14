package com.dc.spider.dao;

import com.dc.spider.pojo.NewsInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsInfoDao extends JpaRepository<NewsInfo,Long> {
}
