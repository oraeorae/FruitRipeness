package com.example.demo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.dao.RobotMapper;
import com.example.demo.pojo.RobotReply;
import com.example.demo.utils.EsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RobotServiceImpl implements RobotService{
    @Autowired
    RobotMapper robotMapper;

    @Override
    public String findAnswer(String chat){
        String answer = null;
        List<RobotReply> list = robotMapper.listFind();
        for( RobotReply rb : list ){
            if( chat.contains(rb.getProblem())){
                answer = robotMapper.findAnswer(rb.getId());
            }
        }
        if( answer == null || "".equals(answer) ){
            answer = "抱歉哦，我还不能理解你想表达的意思 ~";
        }
        return answer;
    }

    @Override
    public List<RobotReply> listFind() {
        return robotMapper.listFind();
    }

    @Override
    public  List<RobotReply> listAll(){
        return robotMapper.listAll();
    }

    //-----以上都是旧方法-----

    @Autowired
    private EsUtils esUtils;

    @Override
    public String esFindAnswer(String chat){
        try{
            JSONObject json = esUtils.searchParticiple("chat",chat,"problem");
            System.out.println(json.toString());
            System.out.println(json.get("hits"));
            JSONObject j = (JSONObject) ((JSONObject) ((JSONArray)((JSONObject) json.get("hits")).get("hits")).get(0)).get("_source");
            System.out.println(j);
            String res = (String)j.get("answer");
            if( res == null || "".equals(res) ){
                return "抱歉哦，我还不能理解你想表达的意思 ~";
            }
            return res;
        }catch (Exception e){
            System.out.println(e);
            return "抱歉哦，我还不能理解你想表达的意思 ~";
        }

    }



}
