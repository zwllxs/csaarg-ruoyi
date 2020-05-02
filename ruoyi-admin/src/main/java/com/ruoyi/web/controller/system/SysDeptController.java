package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.Result;
import com.ruoyi.common.core.domain.Ztree;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.service.ISysDeptService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ruoyi.common.core.domain.Result.custom;
import static com.ruoyi.common.core.domain.Result.error;

/**
 * 部门信息
 *
 * @author ruoyi
 */
@RequestMapping("/system/dept")
@Controller
public class SysDeptController extends BaseController {

  private static final String PREFIX = "system/dept";

  @Autowired
  private ISysDeptService deptService;

  @RequiresPermissions("system:dept:view")
  @GetMapping
  public String dept() {
    return PREFIX + "/dept";
  }

  @RequiresPermissions("system:dept:list")
  @ResponseBody
  @PostMapping("/list")
  public List<SysDept> list(SysDept dept) {
    return deptService.selectDeptList(dept);
  }

  /**
   * 新增部门
   */
  @GetMapping("/add/{parentId}")
  public String add(@PathVariable("parentId") Long parentId, ModelMap mmap) {
    mmap.put("dept", deptService.selectDeptById(parentId));
    return PREFIX + "/add";
  }

  /**
   * 新增保存部门
   */
  @Log(title = "部门管理", businessType = BusinessType.INSERT)
  @RequiresPermissions("system:dept:add")
  @ResponseBody
  @PostMapping("/add")
  public Result addSave(@Validated SysDept dept) {
    if (UserConstants.DEPT_NAME_NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept))) {
      return error("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
    }
    dept.setCreateBy(ShiroUtils.getLoginName());
    return custom(deptService.insertDept(dept));
  }

  /**
   * 修改
   */
  @GetMapping("/edit/{deptId}")
  public String edit(@PathVariable("deptId") Long deptId, ModelMap mmap) {
    SysDept dept = deptService.selectDeptById(deptId);
    if (StringUtils.isNotNull(dept) && 100L == deptId) {
      dept.setParentName("无");
    }
    mmap.put("dept", dept);
    return PREFIX + "/edit";
  }

  /**
   * 保存
   */
  @Log(title = "部门管理", businessType = BusinessType.UPDATE)
  @RequiresPermissions("system:dept:edit")
  @ResponseBody
  @PostMapping("/edit")
  public Result editSave(@Validated SysDept dept) {
    if (UserConstants.DEPT_NAME_NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept))) {
      return error("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
    } else if (dept.getParentId().equals(dept.getDeptId())) {
      return error("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
    }
    dept.setUpdateBy(ShiroUtils.getLoginName());
    return custom(deptService.updateDept(dept));
  }

  /**
   * 删除
   */
  @Log(title = "部门管理", businessType = BusinessType.DELETE)
  @RequiresPermissions("system:dept:remove")
  @ResponseBody
  @GetMapping("/remove/{deptId}")
  public Result remove(@PathVariable("deptId") Long deptId) {
    if (deptService.selectDeptCount(deptId) > 0) {
      return Result.error("存在下级部门,不允许删除");
    }
    if (deptService.checkDeptExistUser(deptId)) {
      return Result.error("部门存在用户,不允许删除");
    }
    return custom(deptService.deleteDeptById(deptId));
  }

  /**
   * 校验部门名称
   */
  @ResponseBody
  @PostMapping("/checkDeptNameUnique")
  public String checkDeptNameUnique(SysDept dept) {
    return deptService.checkDeptNameUnique(dept);
  }

  /**
   * 选择部门树
   */
  @GetMapping("/selectDeptTree/{deptId}")
  public String selectDeptTree(@PathVariable("deptId") Long deptId, ModelMap mmap) {
    mmap.put("dept", deptService.selectDeptById(deptId));
    return PREFIX + "/tree";
  }

  /**
   * 加载部门列表树
   */
  @ResponseBody
  @GetMapping("/treeData")
  public List<Ztree> treeData() {
    return deptService.selectDeptTree(new SysDept());
  }

  /**
   * 加载角色部门（数据权限）列表树
   */
  @ResponseBody
  @GetMapping("/roleDeptTreeData")
  public List<Ztree> deptTreeData(SysRole role) {
    return deptService.roleDeptTreeData(role);
  }
}
