package com.pcwk.ehr.DataController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TrafficController {

    @GetMapping("/traffic/accidents/clist")
    public String getClistPage(Model model) {
        return "accidents/clist"; // src/main/resources/templates/accidents/clist.html 파일을 반환합니다.
    }

    @GetMapping("/traffic/accidents/list")
    public String getListPage(Model model) {
        return "accidents/list"; // src/main/resources/templates/accidents/list.html 파일을 반환합니다.
    }

    @GetMapping("/traffic/accidents/dlist")
    public String getDlistPage(Model model) {
        return "accidents/dlist"; // src/main/resources/templates/accidents/dlist.html 파일을 반환합니다.
    }

    @GetMapping("/traffic/accidents/wlist")
    public String getWlistPage(Model model) {
        return "accidents/wlist"; // src/main/resources/templates/accidents/wlist.html 파일을 반환합니다.
    }
}

