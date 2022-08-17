package com.example.demo.service;

import com.example.demo.dao.CommentsMapper;
import com.example.demo.pojo.Comments;
import com.example.demo.pojo.Community;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentsServiceImpl implements CommentsService{
    @Autowired
    CommentsMapper commentsMapper;

    @Override
    public List<Comments> getComments(int communityId){
        try{
            return commentsMapper.getComments(communityId);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int insertComments(Comments comments){
        return commentsMapper.insertComments(comments);
    }
}
