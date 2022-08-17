package com.example.demo.dao;

import com.example.demo.pojo.Community;
import com.example.demo.pojo.RobotReply;
import com.example.demo.pojo.ScienceEducation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommunityMapper {
    /**
     * 显示文章(最新的在前面)
     */
    @Select("select title,time,id,authorName from data_community order by id desc  limit #{first},#{second} ;")
    List<Community> listCommunity(int first,int second);

    /**
     * 返回具体的文章
     */
    @Select("select * from data_community where id=#{id};")
    Community getCommunity(int id);

    /**
     * 添加新的文章
     */
    @Insert("insert into data_community(title,time,authorId,authorName,body) values(#{title},#{time},#{authorId},#{authorName},#{body})")
    int insertCommunity(Community community);

}
