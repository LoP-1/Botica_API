package quantify.BoticaSaid.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class controlerpruebita {

    @GetMapping("/hello")
    public String hello() {
        return "¡Hola! Accediste con un token válido.";
    }
    @GetMapping("/productos")
    public String productos(){
        return "NO VEAS MIS PRODUCTOS";
    }
}
