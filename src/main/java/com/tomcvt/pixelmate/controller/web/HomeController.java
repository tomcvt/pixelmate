package com.tomcvt.pixelmate.controller.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({ "/", "/home", "" })
    public String home(@AuthenticationPrincipal Object user, Model model) {
        String username = "Guest";
        if (user != null) {
            try {
                var u = (org.springframework.security.core.userdetails.User) user;
                username = u.getUsername();
            } catch (ClassCastException e) {
                // Ignore, keep username as Guest
            }
        }
        model.addAttribute("username", username);
        return "index";
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload-image";
    }
}
