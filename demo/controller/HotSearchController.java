package com.example.demo.controller;

import com.example.demo.service.HotSearchService;
import com.example.demo.utils.JwtUtils;
import com.example.demo.utils.StatusCode;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 科普文章热搜
 * 学习链接： https://blog.csdn.net/chendasheng/article/details/124938229
 * */
@RestController         //注解可以使结果以Json字符串的形式返回给客户端
@RequestMapping(value = "/api/hotsearch")         //使链接还有一个 /api/
public class HotSearchController {
    @Autowired
    HotSearchService hotSearchService;

    @GetMapping(value = "/search")
    @ApiOperation(value = "用户搜索接口", notes = "用户搜索接口")
    public Map<String, Object> giveLike(@RequestParam("find") String find, @RequestParam("token") String token) {
        //获取请求时的token
        JwtUtils jwt = JwtUtils.getInstance();
        Claims claims = jwt.check(token);
        if (claims != null) {
            try{
                //获取openid
                String openid = (String) claims.get("openid");
                hotSearchService.addHotWord(find);
                //redisUtils.sSet("like:user_"+id,openid);
                return StatusCode.success("搜索成功！");

            }catch (Exception e){
                e.printStackTrace();
                return StatusCode.error(3001, "服务器内部错误：" + e.toString());
            }
        }else {
            //非法token
            return StatusCode.error(2001, "用户未登录");
        }
    }

    @GetMapping(value = "/hotsearch")
    @ApiOperation(value = "用户点赞接口", notes = "用户点赞接口")
    public Map<String, Object> giveLike() {
        //获取请求时的token

            try{
                //获取openid
                //String openid = (String) claims.get("openid");

                hotSearchService.getHotWord();
                //redisUtils.sSet("like:user_"+id,openid);
                return StatusCode.success(hotSearchService.getHotWord());

            }catch (Exception e){
                e.printStackTrace();
                return StatusCode.error(3001, "服务器内部错误：" + e.toString());
            }

    }

}
