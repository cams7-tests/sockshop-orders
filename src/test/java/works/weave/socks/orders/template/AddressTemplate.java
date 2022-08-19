package works.weave.socks.orders.template;

import static br.com.six2six.fixturefactory.Fixture.of;
import static lombok.AccessLevel.PRIVATE;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_ADDRESS;
import static works.weave.socks.orders.template.NewOrderResourceTemplate.ADDRESS_ID;

import br.com.six2six.fixturefactory.Rule;
import lombok.NoArgsConstructor;
import works.weave.socks.orders.entities.Address;

@NoArgsConstructor(access = PRIVATE)
public class AddressTemplate {

  public static void loadTemplates() {
    of(Address.class)
        .addTemplate(
            VALID_ADDRESS,
            new Rule() {
              {
                add("id", ADDRESS_ID);
                add("number", "246");
                add("street", "Whitelees Road");
                add("city", "Glasgow");
                add("postcode", "G67 3DL");
                add("country", "United Kingdom");
              }
            });
  }
}
