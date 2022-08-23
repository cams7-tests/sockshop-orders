package works.weave.socks.orders.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.net.URI;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class NewOrderResource {
  @Schema(
      example = "http://user/customers/57a98d98e4b00679b4a830b2",
      required = true,
      description = "URL to get customer")
  @URL
  private URI customer;

  @Schema(
      example = "http://user/addresses/57a98d98e4b00679b4a830b0",
      required = true,
      description = "URL to get customer address")
  @URL
  private URI address;

  @Schema(
      example = "http://user/cards/57a98d98e4b00679b4a830b1",
      required = true,
      description = "URL to get customer card")
  @URL
  private URI card;

  @Schema(
      example = "http://cart/carts/57a98d98e4b00679b4a830b2/items",
      required = true,
      description = "URL to get cart items")
  @URL
  private URI items;
}
