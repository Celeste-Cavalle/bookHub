package tricycle.bookHub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class BookHubApplication {

	public static void main(String[] args) {
		System.out.println("HASH DU MDP ->");
		System.out.println(new BCryptPasswordEncoder().encode("Pa$$w0rd"));
		SpringApplication.run(BookHubApplication.class, args);
	}

}
