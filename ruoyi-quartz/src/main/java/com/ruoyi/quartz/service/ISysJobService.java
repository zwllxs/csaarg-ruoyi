package com.ruoyi.quartz.service;

import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.quartz.domain.SysJob;
import org.quartz.SchedulerException;

import java.util.List;

/**
 * 定时任务调度信息信息 服务层
 *
 * @author ruoyi
 */
public interface ISysJobService {
  /**
   * 获取quartz调度器的计划任务
   *
   * @param job 调度信息
   * @return 调度任务集合
   */
  List<SysJob> selectJobList(SysJob job);

  /**
   * 通过调度任务ID查询调度信息
   *
   * @param jobId 调度任务ID
   * @return 调度任务对象信息
   */
  SysJob selectJobById(Long jobId);

  /**
   * 暂停任务
   *
   * @param job 调度信息
   * @return 结果
   * @throws SchedulerException
   */
  int pauseJob(SysJob job) throws SchedulerException;

  /**
   * 恢复任务
   *
   * @param job 调度信息
   * @return 结果
   * @throws SchedulerException
   */
  int resumeJob(SysJob job) throws SchedulerException;

  /**
   * 删除任务后，所对应的trigger也将被删除
   *
   * @param job 调度信息
   * @return 结果
   * @throws SchedulerException
   */
  int deleteJob(SysJob job) throws SchedulerException;

  /**
   * 批量删除调度信息
   *
   * @param ids 需要删除的数据ID
   * @throws SchedulerException
   */
  void deleteJobByIds(String ids) throws SchedulerException;

  /**
   * 任务调度状态修改
   *
   * @param job 调度信息
   * @return 结果
   * @throws SchedulerException
   */
  int changeStatus(SysJob job) throws SchedulerException;

  /**
   * 立即运行任务
   *
   * @param job 调度信息
   * @throws SchedulerException
   */
  void run(SysJob job) throws SchedulerException;

  /**
   * 新增任务
   *
   * @param job 调度信息
   * @return 结果
   * @throws SchedulerException
   * @throws TaskException
   */
  int insertJob(SysJob job) throws SchedulerException, TaskException;

  /**
   * 更新任务
   *
   * @param job 调度信息
   * @return 结果
   * @throws SchedulerException
   * @throws TaskException
   */
  int updateJob(SysJob job) throws SchedulerException, TaskException;

  /**
   * 校验cron表达式是否有效
   *
   * @param cronExpression 表达式
   * @return 结果
   */
  boolean checkCronExpressionIsValid(String cronExpression);
}