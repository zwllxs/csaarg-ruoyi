package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.SysLogininfor;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统访问日志情况信息 数据层
 *
 * @author ruoyi
 */
public interface SysLogininforMapper extends BaseMapper<SysLogininfor> {

  List<SysLogininfor> page(Page<SysLogininfor> page, @Param("logininfor") SysLogininfor logininfor);

  /**
   * 清空系统登录日志
   *
   * @return 结果
   */
  int cleanLogininfor();
}
