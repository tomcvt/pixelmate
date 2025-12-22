package com.tomcvt.pixelmate.controller.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tomcvt.pixelmate.service.AdminService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN','SUPERUSER')")
public class AdminController {
    @SuppressWarnings("unused")
    private final AdminService adminService;
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    @GetMapping({"", "/"})
    public String adminHome() {
        return "admin/home";
    }
    @GetMapping("/logging")
    public String adminLogs() {
        return "admin/logging-dashboard";
    }
    @GetMapping("/banning")
    public String adminBanning() {
        return "admin/banning-dashboard";
    }
}
