package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.SysConfig;
import org.apache.ibatis.annotations.Param;

/**
 * 参数配置 数据层
 *
 * @author ruoyi
 */
public interface SysConfigMapper extends BaseMapper<SysConfig> {

  IPage<SysConfig> page(Page<SysConfig> page, @Param("config") SysConfig config);
}