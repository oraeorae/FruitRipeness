package com.example.demo.controller;

import com.example.demo.pojo.ScienceEducation;
import com.example.demo.service.ScienceEducationService;
import com.example.demo.utils.FileUploadUtils;
import com.example.demo.utils.StatusCode;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController         //注解可以使结果以Json字符串的形式返回给客户端
@RequestMapping(value = "/api/science")         //使链接还有一个 /api/
public class ScienceEducationController {
    @Autowired
    ScienceEducationService seService;

    @PostMapping("/")
    @ApiOperation(value = "获取科普教育的关键信息")
    public Map<String, Object> listScienceEducation(@RequestParam("page") int page) { //获取前端传过来的code
        try {
            //limit 为10
            List<ScienceEducation> tmp = seService.listScienceEducation(page, 8);
            Map<String, Object> data = StatusCode.success(tmp);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> data = StatusCode.error(3001, "服务器内部错误：" + e.toString());
            return data;
        }
    }

    @PostMapping("/{id}")
    @ApiOperation(value = "获取具体科普教育信息")
    public Map<String, Object> getScienceEducation(@PathVariable(name = "id") String id) {
        try {
            ScienceEducation tmp = seService.getScienceEducation(Integer.parseInt(id));
            Map<String, Object> data = StatusCode.success(tmp);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> data = StatusCode.error(3001, "服务器内部错误：" + e.toString());
            return data;
        }
    }

    @PostMapping("/insert")
    @ApiOperation(value = "插入具体科普信息")
    public Map<String, Object> insertScienceEducation(@RequestParam MultipartFile file, @Valid ScienceEducation se, HttpServletRequest request) {
        try {
            //保存文件
            String filename = FileUploadUtils.SaveServer(file, request);
            //用UUID来生成唯一的id
            //String id = UUID.randomUUID().toString();
            //把实体类中的picture设置成文件路径
            se.setPicture(filename);

            Date today = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date2 = dateFormat.format(today);
            se.setTime(date2);

            seService.insertScienceEducation(se);
            Map<String, Object> data = StatusCode.success("添加成功");


            return data;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> data = StatusCode.error(3001, "服务器内部错误：" + e.toString());
            return data;
        }
    }

}
