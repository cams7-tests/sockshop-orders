package works.weave.socks.orders.entities;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HealthCheck {

  private String service;
  private String status;
  private LocalDateTime date;

  public HealthCheck(String service, String status) {
    this();
    this.service = service;
    this.status = status;
    this.date = LocalDateTime.now();
  }
}
