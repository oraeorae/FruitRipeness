package com.example.demo.service;

import com.example.demo.pojo.ScienceEducation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ScienceEducationService {
    /**
     * 页面只返回一张图片，标题，作者
     */
    List<ScienceEducation> listScienceEducation(int page, int limit);

    /**
     * 页面返回具体信息
     */
    ScienceEducation getScienceEducation(int id);

    /**
     * 添加新的科普信息
     */
     int insertScienceEducation(ScienceEducation se);

}
