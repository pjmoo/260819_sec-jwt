package org.example.secjwt.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MyController {
    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/my")
    public String my() {
        return "my";
    }
}
