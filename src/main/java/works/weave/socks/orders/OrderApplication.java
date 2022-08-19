package works.weave.socks.orders;

import static java.time.ZoneOffset.UTC;
import static java.util.TimeZone.getTimeZone;
import static java.util.TimeZone.setDefault;
import static org.springframework.boot.SpringApplication.run;

import javax.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OrderApplication {

  public static void main(String... args) {
    run(OrderApplication.class, args);
  }

  @PostConstruct
  public void init() {
    setDefault(getTimeZone(UTC));
  }
}
