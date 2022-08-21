package works.weave.socks.orders.entities;

import static java.util.UUID.randomUUID;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Shipment {
  private UUID id;
  private String name;

  public Shipment(String name) {
    this();
    this.id = randomUUID();
    this.name = name;
  }
}
