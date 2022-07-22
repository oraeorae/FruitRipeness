package com.example.demo.service;

import com.example.demo.pojo.Comments;
import com.example.demo.pojo.Community;

import java.util.List;

public interface CommentsService {

    /**
     * 返回具体的文章
     */
    List<Comments> getComments(int communityID);

    int insertComments(Comments comments);
}
