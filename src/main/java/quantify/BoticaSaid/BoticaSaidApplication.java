package quantify.BoticaSaid;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @io.swagger.v3.oas.annotations.info.Info(title = "API Botica Said", version = "1.0"))
public class BoticaSaidApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoticaSaidApplication.class, args);
	}

}
