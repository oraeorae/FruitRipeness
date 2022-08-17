package com.example.demo.service;

import com.example.demo.dao.ScienceEducationMapper;
import com.example.demo.pojo.ScienceEducation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScienceEducationServiceImpl implements ScienceEducationService{
    @Autowired
    ScienceEducationMapper scienceeducationMapper;


    @Override
    public List<ScienceEducation> listScienceEducation(int page, int limit){
        List<ScienceEducation>  listScienceEducation = null;
        int first = (page - 1) * limit;
        int second = limit;
        listScienceEducation = scienceeducationMapper.listScienceEducation(first, second);
        //只返回第一张图片

        for( ScienceEducation t : listScienceEducation ){
            String picture = t.getPicture();
            if(!( picture == null || "".equals(picture) )){
                int f = picture.indexOf("|");
                if( f != -1 ) {
                    t.setPicture(picture.substring(0, f));
                }
            }

        }
        return listScienceEducation;
    }

    @Override
    public ScienceEducation getScienceEducation(int id){
        ScienceEducation se = null;
        se = scienceeducationMapper.getScienceEducation(id);
        return se;
    }

    @Override
    public int insertScienceEducation(ScienceEducation se){
        return scienceeducationMapper.insertScienceEducation(se);
    }
}
