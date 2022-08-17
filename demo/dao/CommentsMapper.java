package com.example.demo.dao;

import com.example.demo.pojo.Comments;
import com.example.demo.pojo.Community;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentsMapper {
    /**
     * 返回具体的评论
     */
    @Select("select * from data_comments where communityId=#{id} order by id desc ;")
    List<Comments> getComments(int communityID);

    /**
     * 添加新的评论
     */
    @Insert("insert into data_comments(communityId,time,authorId,authorName,text) values(#{communityId},#{time},#{authorId},#{authorName},#{text})")
    int insertComments(Comments comments);
}
