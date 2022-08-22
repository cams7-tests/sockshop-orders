package works.weave.socks.orders.entities;

import static org.springframework.data.annotation.AccessType.Type.PROPERTY;
import static works.weave.socks.orders.entities.CustomerOrder.COLLECTION_NAME;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

// curl -XPOST -H 'Content-type: application/json' http://localhost:8082/orders -d '{"customer":
// "http://localhost:8080/customer/1", "address": "http://localhost:8080/address/1", "card":
// "http://localhost:8080/card/1", "items": "http://localhost:8081/carts/1/items"}'

// curl http://localhost:8082/orders/search/customerId\?custId\=1

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = COLLECTION_NAME)
public class CustomerOrder extends RepresentationModel<CustomerOrder> {

  public static final String COLLECTION_NAME = "customerOrder";
  private static final String LINKS_PROPERTY = "_links";

  @Id private String id;
  private String customerId;
  private Customer customer;
  private Address address;
  private Card card;
  private Collection<Item> items;
  private Shipment shipment;
  private LocalDateTime date;
  private float total;

  public CustomerOrder(
      String customerId,
      Customer customer,
      Address address,
      Card card,
      Collection<Item> items,
      Shipment shipment,
      float total) {
    this();
    this.id = null;
    this.customerId = customerId;
    this.customer = customer;
    this.address = address;
    this.card = card;
    this.items = items;
    this.shipment = shipment;
    this.date = LocalDateTime.now();
    this.total = total;
  }

  @AccessType(PROPERTY)
  public void setLinks(List<Link> links) {
    super.removeLinks();
    super.add(links);
  }

  @JsonProperty(LINKS_PROPERTY)
  @Override
  public Links getLinks() {
    return super.getLinks();
  }
}
