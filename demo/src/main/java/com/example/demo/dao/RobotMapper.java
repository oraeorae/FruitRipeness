package com.example.demo.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.demo.pojo.RobotReply;
import java.util.List;

/**
 * @author czh
 */


//create table data_chat(id int not null AUTO_INCREMENT,problem char(100),answer longtext);
@Mapper
public interface RobotMapper {

    //select * from data_chat;
    /**
     * 显示全部的问题及其id
     */
    @Select("select problem,id from data_chat;")
    List<RobotReply> listFind();

    /**
     * 返回具体的问题
     */
    @Select("select answer from data_chat where id=#{id};")
    String findAnswer(int id);
}
