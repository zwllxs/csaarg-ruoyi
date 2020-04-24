package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色信息
 *
 * @author ruoyi
 */
@RequestMapping("/system/role")
@Controller
public class SysRoleController extends BaseController {

  private static final String PREFIX = "system/role";

  @Autowired
  private ISysRoleService roleService;
  @Autowired
  private ISysUserService userService;

  @RequiresPermissions("system:role:view")
  @GetMapping
  public String role() {
    return PREFIX + "/role";
  }

  @RequiresPermissions("system:role:list")
  @ResponseBody
  @PostMapping("/list")
  public TableDataInfo list(SysRole role) {
    startPage();
    List<SysRole> list = roleService.selectRoleList(role);
    return getDataTable(list);
  }

  @Log(title = "角色管理", businessType = BusinessType.EXPORT)
  @RequiresPermissions("system:role:export")
  @ResponseBody
  @PostMapping("/export")
  public AjaxResult export(SysRole role) {
    List<SysRole> list = roleService.selectRoleList(role);
    ExcelUtil<SysRole> util = new ExcelUtil<>(SysRole.class);
    return util.exportExcel(list, "角色数据");
  }

  /**
   * 新增角色
   */
  @GetMapping("/add")
  public String add() {
    return PREFIX + "/add";
  }

  /**
   * 新增保存角色
   */
  @Log(title = "角色管理", businessType = BusinessType.INSERT)
  @RequiresPermissions("system:role:add")
  @ResponseBody
  @PostMapping("/add")
  public AjaxResult addSave(@Validated SysRole role) {
    if (UserConstants.ROLE_NAME_NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
      return error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
    } else if (UserConstants.ROLE_KEY_NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
      return error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
    }
    role.setCreateBy(ShiroUtils.getLoginName());
    ShiroUtils.clearCachedAuthorizationInfo();
    return toAjax(roleService.insertRole(role));

  }

  /**
   * 修改角色
   */
  @GetMapping("/edit/{roleId}")
  public String edit(@PathVariable("roleId") Long roleId, ModelMap mmap) {
    mmap.put("role", roleService.selectRoleById(roleId));
    return PREFIX + "/edit";
  }

  /**
   * 修改保存角色
   */
  @Log(title = "角色管理", businessType = BusinessType.UPDATE)
  @RequiresPermissions("system:role:edit")
  @ResponseBody
  @PostMapping("/edit")
  public AjaxResult editSave(@Validated SysRole role) {
    roleService.checkRoleAllowed(role);
    if (UserConstants.ROLE_NAME_NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
      return error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
    } else if (UserConstants.ROLE_KEY_NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
      return error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
    }
    role.setUpdateBy(ShiroUtils.getLoginName());
    ShiroUtils.clearCachedAuthorizationInfo();
    return toAjax(roleService.updateRole(role));
  }

  /**
   * 角色分配数据权限
   */
  @GetMapping("/authDataScope/{roleId}")
  public String authDataScope(@PathVariable("roleId") Long roleId, ModelMap mmap) {
    mmap.put("role", roleService.selectRoleById(roleId));
    return PREFIX + "/dataScope";
  }

  /**
   * 保存角色分配数据权限
   */
  @Log(title = "角色管理", businessType = BusinessType.UPDATE)
  @RequiresPermissions("system:role:edit")
  @ResponseBody
  @PostMapping("/authDataScope")
  public AjaxResult authDataScopeSave(SysRole role) {
    roleService.checkRoleAllowed(role);
    role.setUpdateBy(ShiroUtils.getLoginName());
    if (roleService.authDataScope(role) > 0) {
      ShiroUtils.setSysUser(userService.selectUserById(ShiroUtils.getSysUser().getUserId()));
      return success();
    }
    return error();
  }

  @Log(title = "角色管理", businessType = BusinessType.DELETE)
  @RequiresPermissions("system:role:remove")
  @ResponseBody
  @PostMapping("/remove")
  public AjaxResult remove(String ids) {
    try {
      return toAjax(roleService.deleteRoleByIds(ids));
    } catch (Exception e) {
      return error(e.getMessage());
    }
  }

  /**
   * 校验角色名称
   */
  @ResponseBody
  @PostMapping("/checkRoleNameUnique")
  public String checkRoleNameUnique(SysRole role) {
    return roleService.checkRoleNameUnique(role);
  }

  /**
   * 校验角色权限
   */
  @ResponseBody
  @PostMapping("/checkRoleKeyUnique")
  public String checkRoleKeyUnique(SysRole role) {
    return roleService.checkRoleKeyUnique(role);
  }

  /**
   * 选择菜单树
   */
  @GetMapping("/selectMenuTree")
  public String selectMenuTree() {
    return PREFIX + "/tree";
  }

  /**
   * 角色状态修改
   */
  @Log(title = "角色管理", businessType = BusinessType.UPDATE)
  @RequiresPermissions("system:role:edit")
  @ResponseBody
  @PostMapping("/changeStatus")
  public AjaxResult changeStatus(SysRole role) {
    roleService.checkRoleAllowed(role);
    return toAjax(roleService.changeStatus(role));
  }

  /**
   * 分配用户
   */
  @RequiresPermissions("system:role:edit")
  @GetMapping("/authUser/{roleId}")
  public String authUser(@PathVariable("roleId") Long roleId, ModelMap mmap) {
    mmap.put("role", roleService.selectRoleById(roleId));
    return PREFIX + "/authUser";
  }

  /**
   * 查询已分配用户角色列表
   */
  @RequiresPermissions("system:role:list")
  @ResponseBody
  @PostMapping("/authUser/allocatedList")
  public TableDataInfo allocatedList(SysUser user) {
    startPage();
    List<SysUser> list = userService.selectAllocatedList(user);
    return getDataTable(list);
  }

  /**
   * 取消授权
   */
  @Log(title = "角色管理", businessType = BusinessType.GRANT)
  @ResponseBody
  @PostMapping("/authUser/cancel")
  public AjaxResult cancelAuthUser(SysUserRole userRole) {
    return toAjax(roleService.deleteAuthUser(userRole));
  }

  /**
   * 批量取消授权
   */
  @Log(title = "角色管理", businessType = BusinessType.GRANT)
  @ResponseBody
  @PostMapping("/authUser/cancelAll")
  public AjaxResult cancelAuthUserAll(Long roleId, String userIds) {
    return toAjax(roleService.deleteAuthUsers(roleId, userIds));
  }

  /**
   * 选择用户
   */
  @GetMapping("/authUser/selectUser/{roleId}")
  public String selectUser(@PathVariable("roleId") Long roleId, ModelMap mmap) {
    mmap.put("role", roleService.selectRoleById(roleId));
    return PREFIX + "/selectUser";
  }

  /**
   * 查询未分配用户角色列表
   */
  @RequiresPermissions("system:role:list")
  @ResponseBody
  @PostMapping("/authUser/unallocatedList")
  public TableDataInfo unallocatedList(SysUser user) {
    startPage();
    List<SysUser> list = userService.selectUnallocatedList(user);
    return getDataTable(list);
  }

  /**
   * 批量选择用户授权
   */
  @Log(title = "角色管理", businessType = BusinessType.GRANT)
  @ResponseBody
  @PostMapping("/authUser/selectAll")
  public AjaxResult selectAuthUserAll(Long roleId, String userIds) {
    return toAjax(roleService.insertAuthUsers(roleId, userIds));
  }
}