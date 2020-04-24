package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.shiro.service.SysPasswordService;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.service.ISysPostService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户信息
 *
 * @author ruoyi
 */
@RequestMapping("/system/user")
@Controller
public class SysUserController extends BaseController {

  private static final String PREFIX = "system/user";

  @Autowired
  private ISysUserService userService;
  @Autowired
  private ISysRoleService roleService;
  @Autowired
  private ISysPostService postService;
  @Autowired
  private SysPasswordService passwordService;

  @RequiresPermissions("system:user:view")
  @GetMapping
  public String user() {
    return PREFIX + "/user";
  }

  @RequiresPermissions("system:user:list")
  @ResponseBody
  @PostMapping("/list")
  public TableDataInfo list(SysUser user) {
    startPage();
    List<SysUser> list = userService.selectUserList(user);
    return getDataTable(list);
  }

  @RequiresPermissions("system:user:export")
  @Log(title = "用户管理", businessType = BusinessType.EXPORT)
  @ResponseBody
  @PostMapping("/export")
  public AjaxResult export(SysUser user) {
    List<SysUser> list = userService.selectUserList(user);
    ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
    return util.exportExcel(list, "用户数据");
  }

  @Log(title = "用户管理", businessType = BusinessType.IMPORT)
  @RequiresPermissions("system:user:import")
  @ResponseBody
  @PostMapping("/importData")
  public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
    ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
    List<SysUser> userList = util.importExcel(file.getInputStream());
    String operName = ShiroUtils.getSysUser().getLoginName();
    String message = userService.importUser(userList, updateSupport, operName);
    return AjaxResult.success(message);
  }

  @RequiresPermissions("system:user:view")
  @ResponseBody
  @GetMapping("/importTemplate")
  public AjaxResult importTemplate() {
    ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
    return util.importTemplateExcel("用户数据");
  }

  /**
   * 新增用户
   */
  @GetMapping("/add")
  public String add(ModelMap mmap) {
    mmap.put("roles", roleService.selectRoleAll());
    mmap.put("posts", postService.selectPostAll());
    return PREFIX + "/add";
  }

  /**
   * 新增保存用户
   */
  @Log(title = "用户管理", businessType = BusinessType.INSERT)
  @RequiresPermissions("system:user:add")
  @ResponseBody
  @PostMapping("/add")
  public AjaxResult addSave(@Validated SysUser user) {
    if (UserConstants.USER_NAME_NOT_UNIQUE.equals(userService.checkLoginNameUnique(user.getLoginName()))) {
      return error("新增用户'" + user.getLoginName() + "'失败，登录账号已存在");
    } else if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
      return error("新增用户'" + user.getLoginName() + "'失败，手机号码已存在");
    } else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
      return error("新增用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
    }
    user.setSalt(ShiroUtils.randomSalt());
    user.setPassword(passwordService.encryptPassword(user.getLoginName(), user.getPassword(), user.getSalt()));
    user.setCreateBy(ShiroUtils.getLoginName());
    return toAjax(userService.insertUser(user));
  }

  /**
   * 修改用户
   */
  @GetMapping("/edit/{userId}")
  public String edit(@PathVariable("userId") Long userId, ModelMap mmap) {
    mmap.put("user", userService.selectUserById(userId));
    mmap.put("roles", roleService.selectRolesByUserId(userId));
    mmap.put("posts", postService.selectPostsByUserId(userId));
    return PREFIX + "/edit";
  }

  /**
   * 修改保存用户
   */
  @Log(title = "用户管理", businessType = BusinessType.UPDATE)
  @RequiresPermissions("system:user:edit")
  @ResponseBody
  @PostMapping("/edit")
  public AjaxResult editSave(@Validated SysUser user) {
    userService.checkUserAllowed(user);
    if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
      return error("修改用户'" + user.getLoginName() + "'失败，手机号码已存在");
    } else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
      return error("修改用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
    }
    user.setUpdateBy(ShiroUtils.getLoginName());
    return toAjax(userService.updateUser(user));
  }

  @Log(title = "重置密码", businessType = BusinessType.UPDATE)
  @RequiresPermissions("system:user:resetPwd")
  @GetMapping("/resetPwd/{userId}")
  public String resetPwd(@PathVariable("userId") Long userId, ModelMap mmap) {
    mmap.put("user", userService.selectUserById(userId));
    return PREFIX + "/resetPwd";
  }

  @Log(title = "重置密码", businessType = BusinessType.UPDATE)
  @RequiresPermissions("system:user:resetPwd")
  @ResponseBody
  @PostMapping("/resetPwd")
  public AjaxResult resetPwdSave(SysUser user) {
    userService.checkUserAllowed(user);
    user.setSalt(ShiroUtils.randomSalt());
    user.setPassword(passwordService.encryptPassword(user.getLoginName(), user.getPassword(), user.getSalt()));
    if (userService.resetUserPwd(user) > 0) {
      if (ShiroUtils.getUserId().longValue() == user.getUserId().longValue()) {
        ShiroUtils.setSysUser(userService.selectUserById(user.getUserId()));
      }
      return success();
    }
    return error();
  }

  /**
   * 进入授权角色页
   */
  @GetMapping("/authRole/{userId}")
  public String authRole(@PathVariable("userId") Long userId, ModelMap mmap) {
    SysUser user = userService.selectUserById(userId);
    // 获取用户所属的角色列表
    List<SysUserRole> userRoles = userService.selectUserRoleByUserId(userId);
    mmap.put("user", user);
    mmap.put("userRoles", userRoles);
    return PREFIX + "/authRole";
  }

  /**
   * 用户授权角色
   */
  @Log(title = "用户管理", businessType = BusinessType.GRANT)
  @RequiresPermissions("system:user:add")
  @ResponseBody
  @PostMapping("/authRole/insertAuthRole")
  public AjaxResult insertAuthRole(Long userId, Long[] roleIds) {
    userService.insertUserAuth(userId, roleIds);
    return success();
  }

  @Log(title = "用户管理", businessType = BusinessType.DELETE)
  @RequiresPermissions("system:user:remove")
  @ResponseBody
  @PostMapping("/remove")
  public AjaxResult remove(String ids) {
    try {
      return toAjax(userService.deleteUserByIds(ids));
    } catch (Exception e) {
      return error(e.getMessage());
    }
  }

  /**
   * 校验用户名
   */
  @ResponseBody
  @PostMapping("/checkLoginNameUnique")
  public String checkLoginNameUnique(SysUser user) {
    return userService.checkLoginNameUnique(user.getLoginName());
  }

  /**
   * 校验手机号码
   */
  @ResponseBody
  @PostMapping("/checkPhoneUnique")
  public String checkPhoneUnique(SysUser user) {
    return userService.checkPhoneUnique(user);
  }

  /**
   * 校验email邮箱
   */
  @ResponseBody
  @PostMapping("/checkEmailUnique")
  public String checkEmailUnique(SysUser user) {
    return userService.checkEmailUnique(user);
  }

  /**
   * 用户状态修改
   */
  @Log(title = "用户管理", businessType = BusinessType.UPDATE)
  @RequiresPermissions("system:user:edit")
  @ResponseBody
  @PostMapping("/changeStatus")
  public AjaxResult changeStatus(SysUser user) {
    userService.checkUserAllowed(user);
    return toAjax(userService.changeStatus(user));
  }
}