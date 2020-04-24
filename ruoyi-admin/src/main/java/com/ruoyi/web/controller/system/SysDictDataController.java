package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysDictData;
import com.ruoyi.system.service.ISysDictDataService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字典信息
 *
 * @author ruoyi
 */
@RequestMapping("/system/dict/data")
@Controller
public class SysDictDataController extends BaseController {

  private static final String PREFIX = "system/dict/data";

  @Autowired
  private ISysDictDataService dictDataService;

  @RequiresPermissions("system:dict:view")
  @GetMapping
  public String dictData() {
    return PREFIX + "/data";
  }

  @RequiresPermissions("system:dict:list")
  @ResponseBody
  @PostMapping("/list")
  public TableDataInfo list(SysDictData dictData) {
    startPage();
    List<SysDictData> list = dictDataService.selectDictDataList(dictData);
    return getDataTable(list);
  }

  @Log(title = "字典数据", businessType = BusinessType.EXPORT)
  @RequiresPermissions("system:dict:export")
  @ResponseBody
  @PostMapping("/export")
  public AjaxResult export(SysDictData dictData) {
    List<SysDictData> list = dictDataService.selectDictDataList(dictData);
    ExcelUtil<SysDictData> util = new ExcelUtil<SysDictData>(SysDictData.class);
    return util.exportExcel(list, "字典数据");
  }

  /**
   * 新增字典类型
   */
  @GetMapping("/add/{dictType}")
  public String add(@PathVariable("dictType") String dictType, ModelMap mmap) {
    mmap.put("dictType", dictType);
    return PREFIX + "/add";
  }

  /**
   * 新增保存字典类型
   */
  @Log(title = "字典数据", businessType = BusinessType.INSERT)
  @RequiresPermissions("system:dict:add")
  @ResponseBody
  @PostMapping("/add")
  public AjaxResult addSave(@Validated SysDictData dict) {
    dict.setCreateBy(ShiroUtils.getLoginName());
    return toAjax(dictDataService.insertDictData(dict));
  }

  /**
   * 修改字典类型
   */
  @GetMapping("/edit/{dictCode}")
  public String edit(@PathVariable("dictCode") Long dictCode, ModelMap mmap) {
    mmap.put("dict", dictDataService.selectDictDataById(dictCode));
    return PREFIX + "/edit";
  }

  /**
   * 修改保存字典类型
   */
  @Log(title = "字典数据", businessType = BusinessType.UPDATE)
  @RequiresPermissions("system:dict:edit")
  @ResponseBody
  @PostMapping("/edit")
  public AjaxResult editSave(@Validated SysDictData dict) {
    dict.setUpdateBy(ShiroUtils.getLoginName());
    return toAjax(dictDataService.updateDictData(dict));
  }

  @Log(title = "字典数据", businessType = BusinessType.DELETE)
  @RequiresPermissions("system:dict:remove")
  @ResponseBody
  @PostMapping("/remove")
  public AjaxResult remove(String ids) {
    return toAjax(dictDataService.deleteDictDataByIds(ids));
  }
}
