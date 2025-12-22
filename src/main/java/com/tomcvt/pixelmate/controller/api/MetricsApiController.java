package com.tomcvt.pixelmate.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tomcvt.pixelmate.dto.DashboardInfoDto;
import com.tomcvt.pixelmate.service.AdminService;

@RestController
@RequestMapping("/api/metrics")
@PreAuthorize("hasAnyRole('ADMIN','SUPERUSER')")
public class MetricsApiController {
    private final AdminService adminService;

    public MetricsApiController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard-info")
    public ResponseEntity<DashboardInfoDto> getDashboardInfo() {
        DashboardInfoDto dashboardInfo = adminService.getDashboardInfo();
        return ResponseEntity.ok(dashboardInfo);
    }
}
