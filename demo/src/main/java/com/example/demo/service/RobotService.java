package com.example.demo.service;

import com.example.demo.pojo.RobotReply;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author czh
 */
public interface RobotService {

    String findAnswer(String chat);
}
