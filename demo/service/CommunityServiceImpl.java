package com.example.demo.service;

import com.example.demo.dao.CommunityMapper;
import com.example.demo.pojo.Community;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityServiceImpl implements CommunityService{
    @Autowired
    CommunityMapper communityMapper;

    @Override
    public List<Community> listCommunity(int page, int limit){
        int first = (page - 1) * limit;
        int second = limit;
        return communityMapper.listCommunity(first,second);
    }


    @Override
    public Community getCommunity(int id){
        return communityMapper.getCommunity(id);
    }


    @Override
    public int insertCommunity(Community community){
        return communityMapper.insertCommunity(community);
    }
}
