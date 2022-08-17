package com.example.demo.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Data       //使用这个注解可以省去代码中大量的get()、 set()、 toString()等方法；
@JsonInclude(JsonInclude.Include.NON_NULL)   // 忽略返回参时值为null的字段
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "chat")
public class Chat {

    /**
     *     index：是否可以作为搜索条件
     *     analyzer：存储时使用的分词器
     *     searchAnalyze：搜索时使用的分词器
     *     store：是否存储
     *     type: 数据类型
     */


    @Id
    @Field(store = true, index = false, type = FieldType.Integer)
    Integer id;
    @Field(store = true, index = true, type = FieldType.Keyword, analyzer = "not_analyzed", searchAnalyzer = "not_analyzed")
    String problem;
    @Field(store = true, index = false, type = FieldType.Keyword, analyzer = "not_analyzed", searchAnalyzer = "not_analyzed")
    String answer;

    public Chat(int _id, String _problem, String _answer) {
        id = _id;
        problem = _problem;
        answer = _answer;
    }
}
