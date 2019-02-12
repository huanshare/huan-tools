package com.huanshare.springMvcDemo.biz;

import com.huanshare.springMvcDemo.dto.GetPageRequestDto;
import com.huanshare.springMvcDemo.entity.CrmUser;
import com.huanshare.springMvcDemo.mapper.CrmUserMapper;
import com.huanshare.tools.mybatis.page.Page;
import com.huanshare.tools.mybatis.page.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liuhuan on 2019/2/11 16:13.
 */
@Service
public class UserBiz {
    @Autowired
    private CrmUserMapper crmUserMapper;

    public Page<CrmUser> getUserPageList(GetPageRequestDto requestModel){
        Pageable pageable=new Pageable(requestModel.getPage(),requestModel.getPageSize());
        return crmUserMapper.getUserPageList(pageable,requestModel);
    }

}
