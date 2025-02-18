package com.pcwk.ehr;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
	
	@GetMapping("/hello.do")
	public String hello() {
		System.out.println("hello()");
		return "index.html";
	}
}
