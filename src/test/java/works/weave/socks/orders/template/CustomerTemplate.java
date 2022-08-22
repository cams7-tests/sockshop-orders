package works.weave.socks.orders.template;

import static br.com.six2six.fixturefactory.Fixture.of;
import static lombok.AccessLevel.PRIVATE;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_CUSTOMER;

import br.com.six2six.fixturefactory.Rule;
import lombok.NoArgsConstructor;
import works.weave.socks.orders.entities.Customer;

@NoArgsConstructor(access = PRIVATE)
public class CustomerTemplate {

  public static final String CUSTOMER_ID = "57a98d98e4b00679b4a830b2";

  public static final String GET_CUSTOMER_URL =
      String.format("http://user/customers/%s", CUSTOMER_ID);
  public static final String GET_CUSTOMER_ADDRESS_URL =
      String.format("http://user/customers/%s/addresses", CUSTOMER_ID);
  public static final String GET_CUSTOMER_CARD_URL =
      String.format("http://user/customers/%s/cards", CUSTOMER_ID);

  public static void loadTemplates() {
    of(Customer.class)
        .addTemplate(
            VALID_CUSTOMER,
            new Rule() {
              {
                add("id", CUSTOMER_ID);
                add("firstName", "User");
                add("lastName", "Name");
                add("username", "user");
              }
            });
  }
}
