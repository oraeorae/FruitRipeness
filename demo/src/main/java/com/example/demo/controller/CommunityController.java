package com.example.demo.controller;

import com.example.demo.Filter.SensitiveFilter;
import com.example.demo.pojo.Comments;
import com.example.demo.pojo.Community;
import com.example.demo.pojo.User;
import com.example.demo.service.CommentsService;
import com.example.demo.service.CommunityService;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtils;
import com.example.demo.utils.StatusCode;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController         //注解可以使结果以Json字符串的形式返回给客户端
@RequestMapping(value = "/api/community")         //使链接还有一个 /api/
public class CommunityController {
    @Autowired
    CommunityService communityService;
    @Autowired
    CommentsService commentsService;
    @Autowired
    UserService userService;

    @PostMapping("/")
    @ApiOperation(value = "获取社区文章的关键信息")
    public Map<String, Object> listCommunity(@RequestParam("page") int page) { //获取前端传过来的code
        try {
            //limit 为10
            List<Community> tmp = communityService.listCommunity(page, 8);
            Map<String, Object> data = StatusCode.success(tmp);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> data = StatusCode.error(3001, "服务器内部错误：" + e.toString());
            return data;
        }
    }


    @PostMapping("/{id}")
    @ApiOperation(value = "获取具体社区文章")
    public Map<String, Object> getCommunity(@PathVariable(name = "id") String id) {
        try {

            Community cy = communityService.getCommunity(Integer.parseInt(id));

            List<Comments> cs = commentsService.getComments(Integer.parseInt(id));

            Map<String, Object> tmp = new HashMap<>();
            tmp.put("article",cy);
            tmp.put("comments",cs);
            Map<String, Object> data = StatusCode.success(tmp);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> data = StatusCode.error(3001, "服务器内部错误：" + e.toString());
            return data;
        }
    }

    @Autowired
    SensitiveFilter sensitiveFilter;

    @PostMapping("/article/insert")
    @ApiOperation(value = "添加具体文章")
    public Map<String, Object> insertCommunity(@Valid Community community,@RequestParam("token") String token,HttpServletRequest request) {
        //获取请求时的token
        JwtUtils jwt = JwtUtils.getInstance();
        Claims claims = jwt.check(token);
        if (claims != null) {
            try {
                //获取头像昵称
                String openid = (String) claims.get("openid");
                User user = userService.getUser(openid);
                community.setAuthorId(user.getOpenid());
                community.setAuthorName(user.getName());
                community.setAuthorUrl(user.getAvatarUrl());

                //敏感词过滤
                community.setBody(sensitiveFilter.filter(community.getBody()));
                community.setTitle(sensitiveFilter.filter(community.getTitle()));

                //设置时间
                Date today = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date2 = dateFormat.format(today);
                community.setTime(date2);

                communityService.insertCommunity(community);
                Map<String, Object> data = StatusCode.success("添加成功");
                return data;
            } catch (Exception e) {
                e.printStackTrace();
                Map<String, Object> data = StatusCode.error(3001, "服务器内部错误：" + e.toString());
                return data;
            }
        }else{
            //非法token
            return StatusCode.error(2001, "用户未登录");
        }
    }

    @PostMapping("/comments/insert")
    @ApiOperation(value = "添加具体评论")
    public Map<String, Object> insertComments(@RequestParam("token") String token,@Valid Comments comments, HttpServletRequest request) {
        //获取请求时的token
        JwtUtils jwt = JwtUtils.getInstance();
        Claims claims = jwt.check(token);
        if (claims != null) {
            try {
                //获取头像昵称
                String openid = (String) claims.get("openid");
                User user = userService.getUser(openid);
                comments.setAuthorId(user.getOpenid());
                comments.setAuthorName(user.getName());
                comments.setAuthorUrl(user.getAvatarUrl());

                //敏感词过滤
                comments.setText(sensitiveFilter.filter(comments.getText()));

                //设置时间
                Date today = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date2 = dateFormat.format(today);
                comments.setTime(date2);

                commentsService.insertComments(comments);
                Map<String, Object> data = StatusCode.success("添加成功");
                return data;
            } catch (Exception e) {
                e.printStackTrace();
                Map<String, Object> data = StatusCode.error(3001, "服务器内部错误：" + e.toString());
                return data;
            }
        }else{
            //非法token
            return StatusCode.error(2001, "用户未登录");
        }
    }
}
