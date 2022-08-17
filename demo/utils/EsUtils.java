package com.example.demo.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.pojo.User;
import org.apache.http.util.EntityUtils;
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
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * elasticsearch工具类
 *
 * 学习链接：
 * https://blog.csdn.net/qq_48033003/article/details/120618901
 * https://blog.csdn.net/weixin_45962741/article/details/120634717
 * https://blog.csdn.net/qq_48033003/article/details/120618901
 * 查询操作： http://events.jianshu.io/p/ef2ea585ea10
 * https://www.it610.com/article/1532791769790443520.htm
 *
 * 索引 可以看做 “数据库”
 * 类型 可以看做 “表”
 * 文档 可以看做 “库中的数据（表中的行）”
 *  @author czh
 */

@Component   //@component是spring中的一个注解，它的作用就是实现bean的注入,注意别漏
public class EsUtils {
    @Autowired
    RestHighLevelClient client;
    /**
     * 索引 可以看做 “数据库”
     * 类型 可以看做 “表”
     * 文档 可以看做 “库中的数据（表中的行）”
     * */

    /**
     * 添加索引，即添加数据库
     * @param index 索引名称，即数据库名称
     * @return 添加索引是否成功
     * @throws Exception
     */
    public boolean addIndex(String index) throws Exception {
        //1.创建新建索引（库） 的请求，定义索引名称
        CreateIndexRequest createIndexRequest= new CreateIndexRequest(index);
        //2.发送请求到ES，获得响应
        CreateIndexResponse createIndexResponse= client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        //3.处理响应结果，判断添加索引是否成功
        return createIndexResponse.isAcknowledged();
    }

    /**
     * 判断索引是否存在
     * @param index 索引名称，即数据库名称
     * @return 判断索引是否存在
     * @throws IOException
     */
    public boolean getIndex(String index) throws IOException {
        //1.创建新建索引（库） 的请求
        GetIndexRequest request= new GetIndexRequest(index);
        //2、判断该索引是否存在
        return client.indices().exists(request,RequestOptions.DEFAULT);
    }

    /**
     * 删除索引
     * @param index 索引名称，即数据库名称
     * @return 判断索引是否删除成功
     * @throws IOException
     */
    public boolean deleteIndex(String index) throws IOException {
        //1.创建新建索引（库） 的请求
        DeleteIndexRequest request= new DeleteIndexRequest(index);
        //2.判断该索引是否存在
        AcknowledgedResponse response= client.indices().delete(request,RequestOptions.DEFAULT);
        //3.处理响应结果，判断删除索引是否成功
        return response.isAcknowledged();
    }

    /**
     * 插入文档 （表中的行）,即向数据库插入数据，第一次插入，第二次覆盖更新
     * @param index  索引名称，即数据库名称
     * @param obj 插入的数据类
     * @param id 数据类id，可以避免重复
     * @throws IOException
     */
    public boolean addDocument(String index,Object obj,String id) throws IOException {
        //1.创建请求,定义请求对象
        IndexRequest indexRequest= new IndexRequest(index);
        //规则
        //id可以不指定 因为es提供了默认的生成策略 如果需要指定，则需要注意id是否重复问题，一旦重复，则会将之前的数据进行覆盖
        indexRequest.id(id);
        indexRequest.timeout(TimeValue.timeValueSeconds(1));
        indexRequest.timeout("1s");
        //2.将json格式字符串放在请求中
        indexRequest.source(JSON.toJSONString(obj), XContentType.JSON);
        //3.发送请求到ES
        IndexResponse response= client.index(indexRequest,RequestOptions.DEFAULT);
        System.out.println(response.toString());
        System.out.println(response.status());
        //这里后期得改一下
        return true;
    }

    /**
     * 获取文档 判断文档是否存在
     * @param index 索引名称，即数据库名称
     * @param id 数据类id
     * @return
     * @throws IOException
     */
    public boolean existDocument(String index,String id) throws IOException {
        //1.创建请求,定义请求对象
        GetRequest getRequest=new GetRequest(index,id);
        //2.不返回source上下文
        //getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        //3.处理响应结果
        return client.existsSource(getRequest,RequestOptions.DEFAULT);
    }

    /**
     * 获取文档内容 根据id获取指定文档
     * @param index 索引名称，即数据库名称
     * @param id 数据类id
     * @return GetResponse返回值
     * @throws IOException
     */
    public GetResponse getDocument(String index,String id) throws IOException {
        //1.创建请求,定义请求对象
        GetRequest getRequest=new GetRequest(index,id);
        //2.处理响应结果
        GetResponse getResponse = client.get(getRequest,RequestOptions.DEFAULT);
        //打印文档的内容
        System.out.println(getResponse.toString());
        System.out.println(getResponse.getSourceAsString());
        return getResponse;
    }

    /**
     * 更新文档记录 更新文档内容
     * @param index 索引名称，即数据库名称
     * @param obj 插入的数据类（空属性值保留原值，非空属性值更新）
     * @param id 数据类id
     * @return Result
     * @throws IOException
     */
    public Result updateDocument(String index, Object obj, String id) throws IOException {
        //1.创建请求,定义请求对象
        UpdateRequest request = new UpdateRequest(index,id);
        request.timeout("1s");
        //2.将数据放入请求json格式
        request.doc(JSON.toJSONString(obj), XContentType.JSON);
        //3.处理响应结果
        UpdateResponse updateResponse = client.update(request,RequestOptions.DEFAULT);
        return updateResponse.getResult();
    }

    /**
     * 删除文档 根据文档id删除指定文档
     * @param index 索引名称，即数据库名称
     * @param id 数据类id
     * @return
     * @throws IOException
     */
    public Result deleteDocument(String index, String id) throws IOException {
        //1.创建请求,定义请求对象
        DeleteRequest request=new DeleteRequest(index,id);
        request.timeout("1s");
        //2.处理响应结果
        DeleteResponse deleteResponse = client.delete(request,RequestOptions.DEFAULT);
        return deleteResponse.getResult();
    }

    /**
     * 真实项目大批量的插入数据
     * @param index 索引名称，即数据库名称
     * @param objList 数据类列表
     * @param idList 数据类列表对应的id
     * @return
     * @throws IOException
     */
    public boolean insertBulkRequest(String index,ArrayList<Object> objList,ArrayList<String> idList) throws IOException {
        //1.创建请求,定义请求对象
        BulkRequest request = new BulkRequest();
        request.timeout("10s");
        //2.批量加入
        for( int i = 0 ; i < objList.size() ; i++ ){
            request.add(
                    new IndexRequest(index)
                            .id(String.valueOf(idList.get(i)))
                            .source(JSON.toJSONString(objList.get(i)),XContentType.JSON));
        }
        //3.处理响应结果
        BulkResponse response = client.bulk(request,RequestOptions.DEFAULT);
        return response.hasFailures();
    }

    public boolean insertBulkRequest(String index,ArrayList<Object> objList) throws IOException {
        //1.创建请求,定义请求对象
        BulkRequest request = new BulkRequest();
        request.timeout("10s");
        //2.批量加入
        for( int i = 0 ; i < objList.size() ; i++ ){
            request.add(
                    new IndexRequest(index)
                            .source(JSON.toJSONString(objList.get(i)),XContentType.JSON));
        }
        //3.处理响应结果
        BulkResponse response = client.bulk(request,RequestOptions.DEFAULT);
        return response.hasFailures();
    }

    //查询参考链接: https://www.it610.com/article/1532791769790443520.htm

    /**
     * 单条件查询，精准匹配
     * @param index 索引名称，即数据库名称
     * @param findName 查询条件的键
     * @param findValue 查询条件的值
     * @return List<Object> 查询到的对象列表
     * @throws Exception
     */
    public List<Object> searchRequest(String index, String findName, String findValue) throws Exception {
        //1.创建请求,定义请求对象  ( SearchRequest 搜索请求 )
        SearchRequest searchRequest = new SearchRequest(index);
        //2.构建搜索条件 ( SearchSourceBuilder 条件构造 )
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //3.查询条件 精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(findName, findValue);
        //4.放入Request对象中
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //5.组装语句
        searchRequest.source(searchSourceBuilder);
        //6.执行查询 返回结果
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        List<Object> list = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            Object obj = JSONObject.parseObject(hit.getSourceAsString(), Object.class);
            list.add(obj);
        }
        return list;
    }

    /**
     * 分页查询
     * @param index 索引名称，即数据库名称
     * @param page 查询的页数
     * @param pageSize 每页的数量
     * @return
     * @throws Exception
     */
    public List<Object> searchByPag(String index,int page,int pageSize) throws Exception {
        //1.定义请求对象
        SearchRequest request = new SearchRequest();
        request.indices(index);
        //2.指定检索条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //3.分页查询数据
        builder.query(QueryBuilders.matchAllQuery());
        int currentPage = page;
        int from = (currentPage - 1) * pageSize;
        builder.from(from);
        builder.size(pageSize);
        request.source(builder);
        //4.发送请求到ES
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //5.处理响应结果
        List<Object> list = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            Object obj = JSONObject.parseObject(hit.getSourceAsString(), Object.class);
            list.add(obj);
        }
        return list;
    }

    /**
     * 排序查询
     * @param index 索引名称，即数据库名称
     * @param sortName 需要排序的键
     * @param order 选择排序方式（升序或者降序）  SortOrder.DESC或者SortOrder.ASC
     * @return
     * @throws Exception
     */
    public List<Object> searchBySort(String index,String sortName,SortOrder order) throws Exception {
        //1.定义请求对象
        SearchRequest request = new SearchRequest();
        request.indices(index);
        //2.指定检索条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchAllQuery());
        //3.根据sortName做降序排序
        //order 可以是 SortOrder.DESC或者SortOrder.ASC
        builder.sort(sortName,order);
        request.source(builder);
        //4.发送请求到ES
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //5.处理响应结果
        List<Object> list = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            Object obj = JSONObject.parseObject(hit.getSourceAsString(), Object.class);
            list.add(obj);
        }
        return list;
    }

    /**
     * 模糊查询
     * @param index 索引名称，即数据库名称
     * @param likeName 模糊查询相似的键
     * @param likeValue 模糊查询相似的值
     * @return
     * @throws Exception
     */
    public List<Object> searchByLike(String index,String likeName,String likeValue) throws Exception {
        //1.定义请求对象
        SearchRequest request = new SearchRequest();
        request.indices(index);
        //2.指定检索条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //和分词无关，这就是和mysql中like类似的做法
        //查询名称中包含“张三”的数据，或者比“张三”多一个字符的数据，这是通过Fuzziness.ONE来控制的，比如“张三1”是可以出现的，但是“张三12”是无法出现的，这是因为他比张三多了两个字符；除了“Fuzziness.ONE”之外，还可以是“Fuzziness.TWO”等
        //名称模糊查询要拼接.keyword
        builder.query(QueryBuilders.fuzzyQuery(likeName.concat(".keyword"), likeValue).fuzziness(Fuzziness.ONE));
        request.source(builder);
        //3.发送请求到ES
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //4.处理响应结果
        List<Object> list = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            Object obj = JSONObject.parseObject(hit.getSourceAsString(), Object.class);
            list.add(obj);
        }
        return list;
    }

    /**
     * 返回分词后的结果
     * 参考链接: https://www.csdn.net/tags/MtTaMg1sNjExODg5LWJsb2cO0O0O.html
     * @param text 要分词的文本
     * @return 分词结果列表
     * @throws IOException
     */
    public List<String> participle(String text) throws IOException {
        List<String> list = new ArrayList<String>();
        Request request = new Request("GET", "_analyze");
        JSONObject entity = new JSONObject();
        entity.put("analyzer", "ik_max_word");
        //entity.put("analyzer", "ik_smart");
        entity.put("text", text);
        request.setJsonEntity(entity.toJSONString());
        Response response = this.client.getLowLevelClient().performRequest(request);
        JSONObject tokens = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        JSONArray arrays = tokens.getJSONArray("tokens");
        for (int i = 0; i < arrays.size(); i++)
        {
            JSONObject obj = JSON.parseObject(arrays.getString(i));
            list.add(obj.getString("token"));
        }
        return list;
    }

    /**
     * 查询全部
     * @param index 索引名称，即数据库名称
     * @return List<Object> 对象列表
     * @throws Exception
     */
    public List<Object> searchAllData(String index) throws Exception {
        //1.定义请求对象
        SearchRequest request = new SearchRequest();
        request.indices(index);
        //2.指定检索条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //3.用来查询索引中全部的数据
        builder.query(QueryBuilders.matchAllQuery());
        request.source(builder);
        //4.发送请求到ES
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //5.处理响应结果
        List<Object> list = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            Object obj = JSONObject.parseObject(hit.getSourceAsString(), Object.class);
            list.add(obj);
        }
        return list;
    }


    //分词查询
    /**
     *
    {
        "query":{
        "match":{
            "problem":{
                "query":"苹果好吃",
                        "operator":"or",
                        "minimum_should_match": 1
            }
        }
    }
    }
     */
    public JSONObject searchParticiple(String index,String search,String findName) throws Exception {
        Request request = new Request("GET", index+"/_search");
        JSONObject third = new JSONObject();            //最深层
        JSONObject second = new JSONObject();           //最中层
        JSONObject first = new JSONObject();            //最低层
        JSONObject zero = new JSONObject();            //最低层
        third.put("query",search);
        third.put("operator","or");
        third.put("minimum_should_match",1);
        second.put(findName,third);
        first.put("match",second);
        zero.put("query",first);
        request.setJsonEntity(zero.toJSONString());
        Response response = client.getLowLevelClient().performRequest(request);
        JSONObject json = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        return json;
    }


    /**
     * 设置文档字段的类型以及分词器
     * @param index 索引名
     * @param type 类型（text或者keyword）
     * @param analyzer 分词器（ik_max_word）
     * @param name 字段名
     * @return
     * @throws Exception
     */
    public boolean setProperties(String index,String type,String analyzer,String name) throws Exception {
        //1.构造请求
        Request request = new Request("PUT", index+"/_mapping/doc?include_type_name=true");
        //2.构造JSON结构体
        JSONObject third = new JSONObject();           //最中层
        JSONObject second = new JSONObject();           //最中层
        JSONObject first = new JSONObject();            //最低层
        third.put("type",type);
        third.put("analyzer",analyzer);     //ik_max_word
        second.put(name,third);
        first.put("properties",second);
        request.setJsonEntity(first.toJSONString());
        Response response = client.getLowLevelClient().performRequest(request);
        JSONObject json = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        return (boolean)json.get("acknowledged");
    }




}
