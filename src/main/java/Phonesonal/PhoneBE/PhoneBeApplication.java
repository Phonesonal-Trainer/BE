package Phonesonal.PhoneBE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PhoneBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhoneBeApplication.class, args);
	}

}
