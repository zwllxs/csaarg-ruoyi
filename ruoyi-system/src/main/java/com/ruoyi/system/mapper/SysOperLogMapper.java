package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.SysOperLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作日志 数据层
 *
 * @author ruoyi
 */
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
  /**
   * 新增操作日志
   *
   * @param operLog 操作日志对象
   */
  void insertOperlog(SysOperLog operLog);

  List<SysOperLog> page(Page<SysOperLog> page, @Param("operLog") SysOperLog operLog);

  /**
   * 查询系统操作日志集合
   *
   * @param operLog 操作日志对象
   * @return 操作日志集合
   */
  List<SysOperLog> selectOperLogList(SysOperLog operLog);

  /**
   * 批量删除系统操作日志
   *
   * @param ids 需要删除的数据
   * @return 结果
   */
  int deleteOperLogByIds(String[] ids);

  /**
   * 查询操作日志详细
   *
   * @param operId 操作ID
   * @return 操作日志对象
   */
  SysOperLog selectOperLogById(Long operId);

  /**
   * 清空操作日志
   */
  void cleanOperLog();
}
