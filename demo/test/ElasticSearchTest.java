package com.example.demo.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.pojo.Chat;
import com.example.demo.pojo.RobotReply;
import com.example.demo.pojo.User;
import com.example.demo.service.RobotService;
import com.example.demo.utils.EsUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticSearchTest {
    @Autowired
    private EsUtils esUtils;

    @Autowired
    private RobotService robotService;
    //测试注入是否成功
    @Test
    public void test(){

    }

    //测试查询索引里面的数据
    @Test
    public void showall() throws Exception {
        List<Object> list = esUtils.searchAllData("chat");
        for( Object o : list ){
            System.out.println(o.toString());
        }
    }

    //添加数据
    @Test
    public void add() throws Exception {
        Chat chat = new Chat(4,"苹果","真好吃");
        boolean a = esUtils.addDocument("chat",chat,chat.getId()+"");
        System.out.println(a);
        System.out.println(esUtils.searchRequest("chat","problem","abcd").toString());
        System.out.println(esUtils.searchRequest("chat","problem.keyword","abcd").toString());
    }

    //批量添加数据,将mysql数据库里的数据全部导入到es中
    @Test
    public void sqladd() throws IOException {
        List<RobotReply> list = robotService.listAll();
        ArrayList l = new ArrayList(list);
        esUtils.insertBulkRequest("chat",l);

    }

    @Test
    public void search() throws Exception {
        System.out.println(esUtils.searchRequest("chat","problem","苹果").toString());
        System.out.println(esUtils.searchRequest("chat","problem.keyword","苹果").toString());
    }

    @Test
    public  void searchp() throws Exception {
        JSONObject json = esUtils.searchParticiple("chat","我香梨和热水一起喝会怎么样","problem");
        System.out.println(json.toString());
        System.out.println(json.get("hits"));
        Object j = ((JSONObject) ((JSONArray)((JSONObject) json.get("hits")).get("hits")).get(0)).get("_source");
        System.out.println(j);
    }

    /**
     * 初始化es数据库
     */
    @Test
    public void initEs(){
        //索引名称
        String index = "chat";
        try{
            if(esUtils.getIndex(index)){
                //如果存在索引，则先删除
                esUtils.deleteIndex(index);
            }
            //创建索引
            esUtils.addIndex(index);
            //设置索引字段的类型以及分词器（重要！！！，否则查询会失败）
            esUtils.setProperties(index,"text","ik_max_word","problem");
            esUtils.setProperties(index,"text","ik_max_word","answer");
            //获取sql数据库的数据，并导入到es中
            List<RobotReply> list = robotService.listAll();
            ArrayList l = new ArrayList(list);
            esUtils.insertBulkRequest("chat",l);
        }catch (Exception e){
            System.out.println(e);
            System.out.println("初始化失败");
        }


    }

}
