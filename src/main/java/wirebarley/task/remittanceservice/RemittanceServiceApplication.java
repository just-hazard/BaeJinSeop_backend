package wirebarley.task.remittanceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RemittanceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RemittanceServiceApplication.class, args);
    }

}
