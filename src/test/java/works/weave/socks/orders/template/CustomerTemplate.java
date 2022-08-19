package works.weave.socks.orders.template;

import static br.com.six2six.fixturefactory.Fixture.of;
import static lombok.AccessLevel.PRIVATE;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_CUSTOMER;
import static works.weave.socks.orders.template.NewOrderResourceTemplate.CUSTOMER_ID;

import br.com.six2six.fixturefactory.Rule;
import lombok.NoArgsConstructor;
import works.weave.socks.orders.entities.Customer;

@NoArgsConstructor(access = PRIVATE)
public class CustomerTemplate {

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
