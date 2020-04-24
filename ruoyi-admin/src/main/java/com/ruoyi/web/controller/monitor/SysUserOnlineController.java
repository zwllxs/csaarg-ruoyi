package com.ruoyi.web.controller.monitor;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.enums.OnlineStatus;
import com.ruoyi.framework.shiro.session.OnlineSession;
import com.ruoyi.framework.shiro.session.OnlineSessionDAO;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUserOnline;
import com.ruoyi.system.service.ISysUserOnlineService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在线用户监控
 *
 * @author ruoyi
 */
@RequestMapping("/monitor/online")
@Controller
public class SysUserOnlineController extends BaseController {

  private static final String PREFIX = "monitor/online";

  @Autowired
  private ISysUserOnlineService userOnlineService;
  @Autowired
  private OnlineSessionDAO onlineSessionDAO;

  @RequiresPermissions("monitor:online:view")
  @GetMapping
  public String online() {
    return PREFIX + "/online";
  }

  @RequiresPermissions("monitor:online:list")
  @ResponseBody
  @PostMapping("/list")
  public TableDataInfo list(SysUserOnline userOnline) {
    startPage();
    List<SysUserOnline> list = userOnlineService.selectUserOnlineList(userOnline);
    return getDataTable(list);
  }

  @Log(title = "在线用户", businessType = BusinessType.FORCE)
  @RequiresPermissions("monitor:online:batchForceLogout")
  @ResponseBody
  @PostMapping("/batchForceLogout")
  public AjaxResult batchForceLogout(@RequestParam("ids[]") String[] ids) {
    for (String sessionId : ids) {
      SysUserOnline online = userOnlineService.selectOnlineById(sessionId);
      if (online == null) {
        return error("用户已下线");
      }
      OnlineSession onlineSession = (OnlineSession) onlineSessionDAO.readSession(online.getSessionId());
      if (onlineSession == null) {
        return error("用户已下线");
      }
      if (sessionId.equals(ShiroUtils.getSessionId())) {
        return error("当前登陆用户无法强退");
      }
      onlineSession.setStatus(OnlineStatus.off_line);
      onlineSessionDAO.update(onlineSession);
      online.setStatus(OnlineStatus.off_line);
      userOnlineService.saveOnline(online);
    }
    return success();
  }

  @Log(title = "在线用户", businessType = BusinessType.FORCE)
  @RequiresPermissions("monitor:online:forceLogout")
  @ResponseBody
  @PostMapping("/forceLogout")
  public AjaxResult forceLogout(String sessionId) {
    SysUserOnline online = userOnlineService.selectOnlineById(sessionId);
    if (sessionId.equals(ShiroUtils.getSessionId())) {
      return error("当前登陆用户无法强退");
    }
    if (online == null) {
      return error("用户已下线");
    }
    OnlineSession onlineSession = (OnlineSession) onlineSessionDAO.readSession(online.getSessionId());
    if (onlineSession == null) {
      return error("用户已下线");
    }
    onlineSession.setStatus(OnlineStatus.off_line);
    onlineSessionDAO.update(onlineSession);
    online.setStatus(OnlineStatus.off_line);
    userOnlineService.saveOnline(online);
    return success();
  }
}
