package com.huanshare.springMvcDemo.mapper;

import com.huanshare.springMvcDemo.dto.GetPageRequestDto;
import com.huanshare.springMvcDemo.entity.CrmUser;
import com.huanshare.tools.mybatis.page.Page;
import com.huanshare.tools.mybatis.page.Pageable;
import org.apache.ibatis.annotations.Param;

public interface CrmUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CrmUser record);

    int insertSelective(CrmUser record);

    CrmUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CrmUser record);

    int updateByPrimaryKey(CrmUser record);

    Page<CrmUser> getUserPageList(Pageable pageable, @Param("model") GetPageRequestDto requestModel);
}