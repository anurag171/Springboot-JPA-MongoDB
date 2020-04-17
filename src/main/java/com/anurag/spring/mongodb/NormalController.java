package com.anurag.spring.mongodb;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NormalController {
	
	@GetMapping(value = "/")
	public String root() {
		return "index";
	}
	
	@PostMapping("/login")
    public String login(Model model) {
		System.out.println(model.toString());
        return "login";
    }

    @GetMapping("/user")
    public String userIndex() {
        return "user/index";
    }

}
