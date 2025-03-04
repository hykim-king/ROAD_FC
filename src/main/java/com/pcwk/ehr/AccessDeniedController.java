package com.pcwk.ehr;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController {

    @GetMapping("/accessDenied")
    public String accessDenied() {
        return "error/accessDenied"; // 접근 거부 페이지로 리턴
    }
}
