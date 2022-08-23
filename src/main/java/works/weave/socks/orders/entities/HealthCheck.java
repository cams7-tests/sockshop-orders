package works.weave.socks.orders.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HealthCheck {

  @Schema(example = "orders-db", description = "Health check service")
  private String service;

  @Schema(example = "OK", description = "Health check status")
  private String status;

  @Schema(example = "2022-08-23T18:32:25.829596", description = "Health check datetime")
  private LocalDateTime date;

  public HealthCheck(String service, String status) {
    this();
    this.service = service;
    this.status = status;
    this.date = LocalDateTime.now();
  }
}
