package works.weave.socks.orders.template;

import static br.com.six2six.fixturefactory.Fixture.of;
import static lombok.AccessLevel.PRIVATE;
import static works.weave.socks.orders.template.CustomerTemplate.CUSTOMER_ID;
import static works.weave.socks.orders.template.DomainTemplateLoader.SHIPMENT;

import br.com.six2six.fixturefactory.Rule;
import java.util.UUID;
import lombok.NoArgsConstructor;
import works.weave.socks.orders.entities.Shipment;

@NoArgsConstructor(access = PRIVATE)
public class ShipmentTemplate {

  public static final String SHIPMENT_ID = "b7df8514-8486-4ed2-b4ee-065fa9a15709";

  public static void loadTemplates() {
    of(Shipment.class)
        .addTemplate(
            SHIPMENT,
            new Rule() {
              {
                add("id", UUID.fromString(SHIPMENT_ID));
                add("name", CUSTOMER_ID);
              }
            });
  }
}
