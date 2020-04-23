package com.ruoyi.web.controller.monitor;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.shiro.service.SysPasswordService;
import com.ruoyi.system.domain.SysLogininfor;
import com.ruoyi.system.service.ISysLogininforService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 系统访问记录
 *
 * @author ruoyi
 */
@RequestMapping("/monitor/logininfor")
@Controller
public class SysLogininforController extends BaseController {

  private String prefix = "monitor/logininfor";

  @Autowired
  private ISysLogininforService logininforService;
  @Autowired
  private SysPasswordService passwordService;

  @RequiresPermissions("monitor:logininfor:view")
  @GetMapping
  public String logininfor() {
    return prefix + "/logininfor";
  }

  @RequiresPermissions("monitor:logininfor:list")
  @ResponseBody
  @PostMapping("/list")
  public TableDataInfo list(SysLogininfor logininfor) {
    startPage();
    List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
    return getDataTable(list);
  }

  @Log(title = "登陆日志", businessType = BusinessType.EXPORT)
  @RequiresPermissions("monitor:logininfor:export")
  @ResponseBody
  @PostMapping("/export")
  public AjaxResult export(SysLogininfor logininfor) {
    List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
    ExcelUtil<SysLogininfor> util = new ExcelUtil<SysLogininfor>(SysLogininfor.class);
    return util.exportExcel(list, "登陆日志");
  }

  @Log(title = "登陆日志", businessType = BusinessType.DELETE)
  @RequiresPermissions("monitor:logininfor:remove")
  @ResponseBody
  @PostMapping("/remove")
  public AjaxResult remove(String ids) {
    return toAjax(logininforService.deleteLogininforByIds(ids));
  }

  @Log(title = "登陆日志", businessType = BusinessType.CLEAN)
  @RequiresPermissions("monitor:logininfor:remove")
  @ResponseBody
  @PostMapping("/clean")
  public AjaxResult clean() {
    logininforService.cleanLogininfor();
    return success();
  }

  @Log(title = "账户解锁", businessType = BusinessType.OTHER)
  @RequiresPermissions("monitor:logininfor:unlock")
  @ResponseBody
  @PostMapping("/unlock")
  public AjaxResult unlock(String loginName) {
    passwordService.unlock(loginName);
    return success();
  }
}
