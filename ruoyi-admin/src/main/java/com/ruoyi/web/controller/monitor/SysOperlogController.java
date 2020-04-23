package com.ruoyi.web.controller.monitor;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.service.ISysOperLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志记录
 *
 * @author ruoyi
 */
@RequestMapping("/monitor/operlog")
@Controller
public class SysOperlogController extends BaseController {

  private String prefix = "monitor/operlog";

  @Autowired
  private ISysOperLogService operLogService;

  @GetMapping
  @RequiresPermissions("monitor:operlog:view")
  public String operlog() {
    return prefix + "/operlog";
  }

  @RequiresPermissions("monitor:operlog:list")
  @ResponseBody
  @PostMapping("/list")
  public TableDataInfo list(SysOperLog operLog) {
    startPage();
    List<SysOperLog> list = operLogService.selectOperLogList(operLog);
    return getDataTable(list);
  }

  @Log(title = "操作日志", businessType = BusinessType.EXPORT)
  @RequiresPermissions("monitor:operlog:export")
  @ResponseBody
  @PostMapping("/export")
  public AjaxResult export(SysOperLog operLog) {
    List<SysOperLog> list = operLogService.selectOperLogList(operLog);
    ExcelUtil<SysOperLog> util = new ExcelUtil<SysOperLog>(SysOperLog.class);
    return util.exportExcel(list, "操作日志");
  }

  @RequiresPermissions("monitor:operlog:remove")
  @ResponseBody
  @PostMapping("/remove")
  public AjaxResult remove(String ids) {
    return toAjax(operLogService.deleteOperLogByIds(ids));
  }

  @RequiresPermissions("monitor:operlog:detail")
  @GetMapping("/detail/{operId}")
  public String detail(@PathVariable("operId") Long operId, ModelMap mmap) {
    mmap.put("operLog", operLogService.selectOperLogById(operId));
    return prefix + "/detail";
  }

  @Log(title = "操作日志", businessType = BusinessType.CLEAN)
  @RequiresPermissions("monitor:operlog:remove")
  @ResponseBody
  @PostMapping("/clean")
  public AjaxResult clean() {
    operLogService.cleanOperLog();
    return success();
  }
}
