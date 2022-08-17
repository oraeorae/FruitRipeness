package com.example.demo.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.RobotService;
import com.example.demo.utils.JwtUtils;
import com.example.demo.utils.StatusCode;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController         //注解可以使结果以Json字符串的形式返回给客户端
@RequestMapping(value = "/api/robot")         //使链接还有一个 /api/
public class RobotController {
    @Autowired
    RobotService robotService;

    @PostMapping("/chat")
    @ApiOperation(value = "返回聊天信息")
    public Map<String, Object> autoReply(@RequestParam("problem") String problem, @RequestParam("token") String token) { //获取前端传过来的code
        //获取请求时的token
        JwtUtils jwt = JwtUtils.getInstance();
        Claims claims = jwt.check(token);
        if (claims != null) {
            String openid = (String) claims.get("openid");
            try {
                Map<String, String> tmp = new HashMap<>();
                //tmp.put("answer",robotService.findAnswer(problem));
                tmp.put("answer",robotService.esFindAnswer(problem));
                Map<String, Object> data = StatusCode.success(tmp);
                return data;
            } catch (Exception e) {
                e.printStackTrace();
                return StatusCode.error(3001, "服务器内部错误：" + e.toString());
            }
        }else{
            //非法token
            return StatusCode.error(2001, "用户未登录");
        }
    }
}
