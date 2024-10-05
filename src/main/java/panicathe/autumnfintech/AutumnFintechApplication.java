package panicathe.autumnfintech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AutumnFintechApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutumnFintechApplication.class, args);
    }

}
