package quantify.BoticaSaid.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rico")
public class rico {

    @GetMapping("/")
    public String hello() {
        return "QUE RICOOOOOOOOOO!!";
    }

}
