package UnitSystem.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
@SpringBootApplication
@EnableCaching
@EnableWebSocketMessageBroker
@EnableAsync
public class HelwanApplication {
	public static void main(String[] args) {
		SpringApplication.run(HelwanApplication.class, args);
	}

}
