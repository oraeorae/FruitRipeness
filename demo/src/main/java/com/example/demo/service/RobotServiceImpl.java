package com.example.demo.service;

import com.example.demo.dao.RobotMapper;
import com.example.demo.pojo.RobotReply;
import org.springframework.beans.factory.annotation.Autowired;
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
}
