package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.SysPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 岗位信息 数据层
 *
 * @author ruoyi
 */
public interface SysPostMapper extends BaseMapper<SysPost> {

  List<SysPost> page(Page<SysPost> page, @Param("post") SysPost post);

  /**
   * 根据用户ID查询岗位
   *
   * @param userId 用户ID
   * @return 岗位列表
   */
  List<SysPost> listByUser(Long userId);
}
