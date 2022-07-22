package com.example.demo.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @title 社区文章评论
 * @author czh
 * 文章ID
 * 发布时间
 * 发布者昵称
 * 发布者ID
 * 评论
 * 评论ID
 */
//create table data_community(title char(100),time char(100),author char(100),body longtext,id char(100));
@Data       //使用这个注解可以省去代码中大量的get()、 set()、 toString()等方法；
@JsonInclude(JsonInclude.Include.NON_NULL)   // 忽略返回参时值为null的字段
public class Comments {
    String communityId;
    String time;
    String authorId;
    String authorName;
    String authorUrl;
    String text;
    int id;
}
