package works.weave.socks.orders.resources;

import java.net.URI;
import org.hibernate.validator.constraints.URL;

public class NewOrderResource {
  @URL public URI customer;
  @URL public URI address;
  @URL public URI card;
  @URL public URI items;
}
