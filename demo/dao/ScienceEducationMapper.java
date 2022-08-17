package com.example.demo.dao;

import com.example.demo.pojo.ScienceEducation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @title 科普教育
 * @author czh
 */

//create table data_article(id int not null AUTO_INCREMENT,title char(100),time char(100),author char(100),body longtext);
@Mapper
public interface ScienceEducationMapper {

    /**
     * 页面只返回一张图片，标题，作者
     */
    @Select("select id,title,author,picture from data_article order by id desc limit #{first},#{second};")
    List<ScienceEducation> listScienceEducation(int first, int second);

    /**
     * 页面返回具体信息
     */
    @Select("select * from data_article where id=#{id};")
    ScienceEducation getScienceEducation(int id);

    /**
     * 添加新的科普信息
     */
    @Insert("insert into data_article(title,time,author,body,picture) values(#{title},#{time},#{author},#{body},#{picture})")
    int insertScienceEducation(ScienceEducation se);


}
