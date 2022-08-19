package works.weave.socks.orders.values;

import lombok.Data;
import lombok.NoArgsConstructor;
import works.weave.socks.orders.entities.Address;
import works.weave.socks.orders.entities.Card;
import works.weave.socks.orders.entities.Customer;

@Data
@NoArgsConstructor
public class PaymentRequest {
  private Address address;
  private Card card;
  private Customer customer;
  private float amount;

  public PaymentRequest(Address address, Card card, Customer customer, float amount) {
    this();
    this.address = address;
    this.customer = customer;
    this.card = card;
    this.amount = amount;
  }
}
