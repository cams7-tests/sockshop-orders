package works.weave.socks.orders.template;

import static br.com.six2six.fixturefactory.Fixture.from;
import static br.com.six2six.fixturefactory.Fixture.of;
import static java.time.LocalDateTime.parse;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.List.of;
import static lombok.AccessLevel.PRIVATE;
import static works.weave.socks.orders.template.CustomerTemplate.CUSTOMER_ID;
import static works.weave.socks.orders.template.DomainTemplateLoader.SHIPMENT;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_ADDRESS;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_CARD;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_CUSTOMER;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_CUSTOMER_ORDER;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_ITEM;

import br.com.six2six.fixturefactory.Rule;
import lombok.NoArgsConstructor;
import works.weave.socks.orders.entities.Address;
import works.weave.socks.orders.entities.Card;
import works.weave.socks.orders.entities.Customer;
import works.weave.socks.orders.entities.CustomerOrder;
import works.weave.socks.orders.entities.Item;
import works.weave.socks.orders.entities.Shipment;

@NoArgsConstructor(access = PRIVATE)
public class CustomerOrderTemplate {

  public static final String CUSTOMER_ORDER_ID = "6303c1ee65468d793b17e711";
  public static final String GET_CUSTOMER_ORDER_URL =
      String.format("http://localhost/orders/%s", CUSTOMER_ORDER_ID);

  public static final float SHIPPING = 4.99f;

  public static void loadTemplates() {
    of(CustomerOrder.class)
        .addTemplate(
            VALID_CUSTOMER_ORDER,
            new Rule() {
              {
                add("id", CUSTOMER_ORDER_ID);
                add("customerId", CUSTOMER_ID);
                add("customer", (Customer) from(Customer.class).gimme(VALID_CUSTOMER));
                add("address", (Address) from(Address.class).gimme(VALID_ADDRESS));
                add("card", (Card) from(Card.class).gimme(VALID_CARD));
                add("items", of((Item) from(Item.class).gimme(VALID_ITEM)));
                add("shipment", (Shipment) from(Shipment.class).gimme(SHIPMENT));
                add("date", parse("2022-08-22T18:13:10.903361", ISO_DATE_TIME));
                add("total", (3 * 18.0f) + SHIPPING);
              }
            });
  }
}
