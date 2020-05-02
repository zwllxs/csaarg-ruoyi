package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.Result;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.service.ISysPostService;
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
 * 岗位信息操作处理
 *
 * @author ruoyi
 */
@RequestMapping("/system/post")
@Controller
public class SysPostController extends BaseController {

  private static final String PREFIX = "system/post";

  @Autowired
  private ISysPostService postService;

  @RequiresPermissions("system:post:view")
  @GetMapping
  public String operlog() {
    return PREFIX + "/post";
  }

  @RequiresPermissions("system:post:list")
  @ResponseBody
  @PostMapping("/list")
  public Result list(Page<SysPost> page, SysPost post) {
    return Result.success(postService.page(page, post));
  }

  @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
  @RequiresPermissions("system:post:export")
  @ResponseBody
  @PostMapping("/export")
  public Result export(SysPost post) {
    List<SysPost> list = postService.selectPostList(post);
    ExcelUtil<SysPost> util = new ExcelUtil<>(SysPost.class);
    return util.exportExcel(list, "岗位数据");
  }

  @Log(title = "岗位管理", businessType = BusinessType.DELETE)
  @RequiresPermissions("system:post:remove")
  @ResponseBody
  @PostMapping("/remove")
  public Result remove(String ids) {
    try {
      return custom(postService.deletePostByIds(ids));
    } catch (Exception e) {
      return error(e.getMessage());
    }
  }

  /**
   * 新增岗位
   */
  @GetMapping("/add")
  public String add() {
    return PREFIX + "/add";
  }

  /**
   * 新增保存岗位
   */
  @Log(title = "岗位管理", businessType = BusinessType.INSERT)
  @RequiresPermissions("system:post:add")
  @ResponseBody
  @PostMapping("/add")
  public Result addSave(@Validated SysPost post) {
    if (UserConstants.POST_NAME_NOT_UNIQUE.equals(postService.checkPostNameUnique(post))) {
      return error("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
    } else if (UserConstants.POST_CODE_NOT_UNIQUE.equals(postService.checkPostCodeUnique(post))) {
      return error("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
    }
    post.setCreateBy(ShiroUtils.getLoginName());
    return custom(postService.insertPost(post));
  }

  /**
   * 修改岗位
   */
  @GetMapping("/edit/{postId}")
  public String edit(@PathVariable("postId") Long postId, ModelMap mmap) {
    mmap.put("post", postService.selectPostById(postId));
    return PREFIX + "/edit";
  }

  /**
   * 修改保存岗位
   */
  @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
  @RequiresPermissions("system:post:edit")
  @ResponseBody
  @PostMapping("/edit")
  public Result editSave(@Validated SysPost post) {
    if (UserConstants.POST_NAME_NOT_UNIQUE.equals(postService.checkPostNameUnique(post))) {
      return error("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
    } else if (UserConstants.POST_CODE_NOT_UNIQUE.equals(postService.checkPostCodeUnique(post))) {
      return error("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
    }
    post.setUpdateBy(ShiroUtils.getLoginName());
    return custom(postService.updatePost(post));
  }

  /**
   * 校验岗位名称
   */
  @ResponseBody
  @PostMapping("/checkPostNameUnique")
  public String checkPostNameUnique(SysPost post) {
    return postService.checkPostNameUnique(post);
  }

  /**
   * 校验岗位编码
   */
  @ResponseBody
  @PostMapping("/checkPostCodeUnique")
  public String checkPostCodeUnique(SysPost post) {
    return postService.checkPostCodeUnique(post);
  }
}
