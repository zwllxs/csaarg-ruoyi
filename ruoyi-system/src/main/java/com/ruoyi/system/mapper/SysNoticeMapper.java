package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.SysNotice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告 数据层
 *
 * @author ruoyi
 */
public interface SysNoticeMapper extends BaseMapper<SysNotice> {

  List<SysNotice> page(Page<SysNotice> page, @Param("notice") SysNotice notice);
}