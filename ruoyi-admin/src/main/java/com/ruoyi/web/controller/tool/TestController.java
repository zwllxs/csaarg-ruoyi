package com.ruoyi.web.controller.tool;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * swagger 用户测试方法
 *
 * @author ruoyi
 */
@Api("用户信息管理")
@RequestMapping("/test/user")
@RestController
public class TestController extends BaseController {

  private final static Map<Integer, UserEntity> users = new LinkedHashMap<Integer, UserEntity>();

  {
    users.put(1, new UserEntity(1, "admin", "admin123", "15888888888"));
    users.put(2, new UserEntity(2, "ry", "admin123", "15666666666"));
  }

  @ApiOperation("获取用户列表")
  @GetMapping("/list")
  public AjaxResult userList() {
    List<UserEntity> userList = new ArrayList<UserEntity>(users.values());
    return AjaxResult.success(userList);
  }

  @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "int", paramType = "path")
  @ApiOperation("获取用户详细")
  @GetMapping("/{userId}")
  public AjaxResult getUser(@PathVariable Integer userId) {
    if (!users.isEmpty() && users.containsKey(userId)) {
      return AjaxResult.success(users.get(userId));
    } else {
      return error("用户不存在");
    }
  }

  @ApiImplicitParam(name = "userEntity", value = "新增用户信息", dataType = "UserEntity")
  @ApiOperation("新增用户")
  @PostMapping("/save")
  public AjaxResult save(UserEntity user) {
    if (StringUtils.isNull(user) || StringUtils.isNull(user.getUserId())) {
      return error("用户ID不能为空");
    }
    return AjaxResult.success(users.put(user.getUserId(), user));
  }

  @ApiImplicitParam(name = "userEntity", value = "新增用户信息", dataType = "UserEntity")
  @ApiOperation("更新用户")
  @PutMapping("/update")
  public AjaxResult update(UserEntity user) {
    if (StringUtils.isNull(user) || StringUtils.isNull(user.getUserId())) {
      return error("用户ID不能为空");
    }
    if (users.isEmpty() || !users.containsKey(user.getUserId())) {
      return error("用户不存在");
    }
    users.remove(user.getUserId());
    return AjaxResult.success(users.put(user.getUserId(), user));
  }

  @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "int", paramType = "path")
  @ApiOperation("删除用户信息")
  @DeleteMapping("/{userId}")
  public AjaxResult delete(@PathVariable Integer userId) {
    if (!users.isEmpty() && users.containsKey(userId)) {
      users.remove(userId);
      return success();
    } else {
      return error("用户不存在");
    }
  }
}

@ApiModel("用户实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
class UserEntity {
  @ApiModelProperty("用户ID")
  private Integer userId;

  @ApiModelProperty("用户名称")
  private String username;

  @ApiModelProperty("用户密码")
  private String password;

  @ApiModelProperty("用户手机")
  private String mobile;
}
