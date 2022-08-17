package com.example.demo.service;

import com.example.demo.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 热搜版
 * 参考链接：
 * https://blog.csdn.net/afreon/article/details/124240119
 * https://www.freesion.com/article/7875561664/
 * https://blog.csdn.net/XinhuaShuDiao/article/details/84906538
 * */
@Transactional
@Service("redisService")
public class HotSearchService {
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 设置缓存失效时间，统一为凌晨零点
     * @param hotWord
     * @throws Exception
     */
    public void addHotWord(String hotWord) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.MILLISECOND,0);
        //晚上十二点与当前时间的毫秒差
        Long timeOut = (calendar.getTimeInMillis()-System.currentTimeMillis()) / 1000;
        redisUtils.expire("hotWord",timeOut);
        // 加入排序set
        redisUtils.incrementScore("hotWord", hotWord, 1);
    }


    /**
     * 获取热词前五位
     * @return
     */
    public List<String> getHotWord() {
        List<String> hotWordList = new ArrayList<>();
        Set<Object> value = redisUtils.reverseRangeByScore("hotWord",1,100);
        int flag = 0;
        for( Object val : value ){
            flag++;
            hotWordList.add((String)val);
            if ( flag >= 5 ){
                break;
            }
        }
        return hotWordList;
    }



}
