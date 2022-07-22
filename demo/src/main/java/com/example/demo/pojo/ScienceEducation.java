package com.example.demo.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @title 科普教育
 * @author czh
 * id唯一标识符
 * 标题
 * 发布时间
 * 发布作者
 * 正文主体
 * 图片
 */
@Data       //使用这个注解可以省去代码中大量的get()、 set()、 toString()等方法；
@JsonInclude(JsonInclude.Include.NON_NULL)   // 忽略返回参时值为null的字段
//create table data_article(title char(100),time char(100),author char(100),body longtext);
public class ScienceEducation {
    int id;
    String title;
    String time;
    String author;
    String body;
    String picture;
}
