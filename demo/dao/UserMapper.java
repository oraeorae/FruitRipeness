package com.example.demo.dao;

import com.example.demo.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
//create table users(avatarUrl longtext not null,name varchar(50),openid varchar(70),primary key (openid));
public interface UserMapper {
    /**
     * 判断用户是否存在
     */
    @Select("SELECT count(*) FROM users WHERE openid=#{openid}")
    int isUser(String openid);

    /**
     * 添加新的用户信息
     */
    @Insert("insert into users(openid) values(#{openid})")
    int insertUser(User user);
    //@Insert("insert into users(avatarUrl,name,openid) values(#{dishes},#{avatarUrl},#{name},#{openid})")
    //int insertUser(User user);

    /**
     * 返回用户信息
     */
    @Select("select * from users WHERE openid=#{openid}")
    User getUser(String openid);

}