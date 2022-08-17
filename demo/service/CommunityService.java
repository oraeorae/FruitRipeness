package com.example.demo.service;

import com.example.demo.pojo.Community;
import com.example.demo.pojo.ScienceEducation;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CommunityService {
    /**
     * 显示文章
     */
    List<Community> listCommunity(int page, int limit);

    /**
     * 返回具体的文章
     */
    Community getCommunity(int id);


    int insertCommunity(Community community);
}
