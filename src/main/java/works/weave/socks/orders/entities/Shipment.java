package works.weave.socks.orders.entities;

import static java.util.UUID.randomUUID;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document
public class Shipment {

  @Id private UUID id;
  private String name;

  public Shipment(String name) {
    this();
    this.id = randomUUID();
    this.name = name;
  }
}
