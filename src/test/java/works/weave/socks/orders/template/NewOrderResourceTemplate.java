package works.weave.socks.orders.template;

import static br.com.six2six.fixturefactory.Fixture.of;
import static java.net.URI.create;
import static lombok.AccessLevel.PRIVATE;
import static works.weave.socks.orders.template.AddressTemplate.GET_ADDRESS_URL;
import static works.weave.socks.orders.template.CardTemplate.GET_CARD_URL;
import static works.weave.socks.orders.template.CustomerTemplate.CUSTOMER_ID;
import static works.weave.socks.orders.template.CustomerTemplate.GET_CUSTOMER_URL;
import static works.weave.socks.orders.template.DomainTemplateLoader.INVALID_NEW_ORDER_RESOURCE;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_NEW_ORDER_RESOURCE;

import br.com.six2six.fixturefactory.Rule;
import lombok.NoArgsConstructor;
import works.weave.socks.orders.resources.NewOrderResource;

@NoArgsConstructor(access = PRIVATE)
public class NewOrderResourceTemplate {

  public static final String GET_ITEMS_URL =
      String.format("http://cart/carts/%s/items", CUSTOMER_ID);
  public static final String INVALID_CUSTOMER_URL = "http://user/customers/!#$";

  public static void loadTemplates() {
    of(NewOrderResource.class)
        .addTemplate(
            VALID_NEW_ORDER_RESOURCE,
            new Rule() {
              {
                add("customer", create(GET_CUSTOMER_URL));
                add("address", create(GET_ADDRESS_URL));
                add("card", create(GET_CARD_URL));
                add("items", create(GET_ITEMS_URL));
              }
            })
        .addTemplate(INVALID_NEW_ORDER_RESOURCE)
        .inherits(
            VALID_NEW_ORDER_RESOURCE,
            new Rule() {
              {
                add("customer", create(INVALID_CUSTOMER_URL));
              }
            });
  }
}
