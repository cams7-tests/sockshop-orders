package works.weave.socks.orders.resources;

import java.net.URI;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class NewOrderResource {
  @URL private URI customer;
  @URL private URI address;
  @URL private URI card;
  @URL private URI items;
}
