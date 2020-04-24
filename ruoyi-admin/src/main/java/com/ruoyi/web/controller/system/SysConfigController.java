package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.service.ISysConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 参数配置 信息操作处理
 *
 * @author ruoyi
 */
@RequestMapping("/system/config")
@Controller
public class SysConfigController extends BaseController {

  private static final String PREFIX = "system/config";

  @Autowired
  private ISysConfigService configService;

  @RequiresPermissions("system:config:view")
  @GetMapping
  public String config() {
    return PREFIX + "/config";
  }

  /**
   * 查询参数配置列表
   */
  @RequiresPermissions("system:config:list")
  @ResponseBody
  @PostMapping("/list")
  public TableDataInfo list(SysConfig config) {
    startPage();
    List<SysConfig> list = configService.selectConfigList(config);
    return getDataTable(list);
  }

  @Log(title = "参数管理", businessType = BusinessType.EXPORT)
  @RequiresPermissions("system:config:export")
  @ResponseBody
  @PostMapping("/export")
  public AjaxResult export(SysConfig config) {
    List<SysConfig> list = configService.selectConfigList(config);
    ExcelUtil<SysConfig> util = new ExcelUtil<>(SysConfig.class);
    return util.exportExcel(list, "参数数据");
  }

  /**
   * 新增参数配置
   */
  @GetMapping("/add")
  public String add() {
    return PREFIX + "/add";
  }

  /**
   * 新增保存参数配置
   */
  @Log(title = "参数管理", businessType = BusinessType.INSERT)
  @RequiresPermissions("system:config:add")
  @ResponseBody
  @PostMapping("/add")
  public AjaxResult addSave(@Validated SysConfig config) {
    if (UserConstants.CONFIG_KEY_NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
      return error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
    }
    config.setCreateBy(ShiroUtils.getLoginName());
    return toAjax(configService.insertConfig(config));
  }

  /**
   * 修改参数配置
   */
  @GetMapping("/edit/{configId}")
  public String edit(@PathVariable("configId") Long configId, ModelMap mmap) {
    mmap.put("config", configService.selectConfigById(configId));
    return PREFIX + "/edit";
  }

  /**
   * 修改保存参数配置
   */
  @Log(title = "参数管理", businessType = BusinessType.UPDATE)
  @RequiresPermissions("system:config:edit")
  @ResponseBody
  @PostMapping("/edit")
  public AjaxResult editSave(@Validated SysConfig config) {
    if (UserConstants.CONFIG_KEY_NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
      return error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
    }
    config.setUpdateBy(ShiroUtils.getLoginName());
    return toAjax(configService.updateConfig(config));
  }

  /**
   * 删除参数配置
   */
  @Log(title = "参数管理", businessType = BusinessType.DELETE)
  @RequiresPermissions("system:config:remove")
  @ResponseBody
  @PostMapping("/remove")
  public AjaxResult remove(String ids) {
    return toAjax(configService.deleteConfigByIds(ids));
  }

  /**
   * 清空缓存
   */
  @Log(title = "参数管理", businessType = BusinessType.CLEAN)
  @RequiresPermissions("system:config:remove")
  @ResponseBody
  @GetMapping("/clearCache")
  public AjaxResult clearCache() {
    configService.clearCache();
    return success();
  }

  /**
   * 校验参数键名
   */
  @ResponseBody
  @PostMapping("/checkConfigKeyUnique")
  public String checkConfigKeyUnique(SysConfig config) {
    return configService.checkConfigKeyUnique(config);
  }
}
