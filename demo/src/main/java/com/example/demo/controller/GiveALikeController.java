package com.example.demo.controller;

import com.example.demo.utils.JwtUtils;
import com.example.demo.utils.RedisUtils;
import com.example.demo.utils.StatusCode;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 科普文章点赞功能
 * */
@RestController         //注解可以使结果以Json字符串的形式返回给客户端
@RequestMapping(value = "/api/science")         //使链接还有一个 /api/
public class GiveALikeController {
    @Autowired
    private RedisUtils redisUtils;

    @GetMapping(value = "/giveLike/{id}")
    @ApiOperation(value = "用户点赞接口", notes = "用户点赞接口")
    public Map<String, Object> giveLike(@PathVariable String id,@RequestParam("token") String token) {
        //获取请求时的token
        JwtUtils jwt = JwtUtils.getInstance();
        Claims claims = jwt.check(token);
        if (claims != null) {
            try{
                //获取openid
                String openid = (String) claims.get("openid");
                redisUtils.sSet("like:user_"+id,openid);
                return StatusCode.success("点赞成功！");

            }catch (Exception e){
                e.printStackTrace();
                return StatusCode.error(3001, "服务器内部错误：" + e.toString());
            }
        }else {
            //非法token
            return StatusCode.error(2001, "用户未登录");
        }
    }

    @GetMapping(value = "/giveCancelLike/{id}")
    @ApiOperation(value = "用户取消点赞接口", notes = "用户取消点赞接口")
    public Map<String, Object> giveCancelLike(@PathVariable String id,@RequestParam("token") String token) {
        //获取请求时的token
        JwtUtils jwt = JwtUtils.getInstance();
        Claims claims = jwt.check(token);
        if (claims != null) {
            try{
                //获取openid
                String openid = (String) claims.get("openid");
                redisUtils.setRemove("like:user_"+id,openid);
                return StatusCode.success("取消成功!");

            }catch (Exception e){
                e.printStackTrace();
                return StatusCode.error(3001, "服务器内部错误：" + e.toString());
            }
        }else {
            //非法token
            return StatusCode.error(2001, "用户未登录");
        }
    }

    @GetMapping(value = "/isGiveLike/{id}")
    @ApiOperation(value = "判断用户是否点赞", notes = "判断用户是否点赞")
    public Map<String, Object> isGiveLike(@PathVariable String id, @RequestParam("token") String token) {
        //获取请求时的token
        JwtUtils jwt = JwtUtils.getInstance();
        Claims claims = jwt.check(token);
        if (claims != null) {
            try{
                //获取openid
                String openid = (String) claims.get("openid");
                boolean member = redisUtils.sHasKey("like:user_"+id, openid);
                if (member) {
                    return StatusCode.success("已点赞");
                }else{
                    return StatusCode.success("未点赞");
                }
            }catch (Exception e){
                e.printStackTrace();
                return StatusCode.error(3001, "服务器内部错误：" + e.toString());
            }
        }else {
            //非法token
            return StatusCode.error(2001, "用户未登录");
        }
    }

    @GetMapping(value = "/getGiveLikeList/{id}")
    @ApiOperation(value = "获取点赞列表", notes = "获取点赞列表")
    public Map<String, Object> getGiveLikeList(@PathVariable String id) {
        Set<Object> set = redisUtils.sGet("like:user_"+id);
        Map<String,Object> tmp = new HashMap<>();
        tmp.put("people",set);
        return StatusCode.success(tmp);

    }

    @GetMapping(value = "/getGiveLikeSize/{id}")
    @ApiOperation(value = "获取点赞人数", notes = "获取点赞人数")
    public Map<String, Object> getGiveLikeSize(@PathVariable String id) {
        Long aLong = redisUtils.sGetSetSize("like:user_"+id);
        Map<String,String> tmp = new HashMap<>();
        tmp.put("num",aLong.toString());
        return StatusCode.success(tmp);
    }
}
