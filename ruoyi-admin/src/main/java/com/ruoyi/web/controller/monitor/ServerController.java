package com.ruoyi.web.controller.monitor;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.framework.web.domain.Server;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 服务器监控
 *
 * @author ruoyi
 */
@RequestMapping("/monitor/server")
@Controller
public class ServerController extends BaseController {

  private static final String PREFIX = "monitor/server";

  @RequiresPermissions("monitor:server:view")
  @GetMapping
  public String server(ModelMap mmap) throws Exception {
    Server server = new Server();
    server.copyTo();
    mmap.put("server", server);
    return PREFIX + "/server";
  }
}
