package works.weave.socks.orders.template;

import static br.com.six2six.fixturefactory.Fixture.of;
import static lombok.AccessLevel.PRIVATE;
import static works.weave.socks.orders.template.DomainTemplateLoader.INVALID_ITEM;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_ITEM;

import br.com.six2six.fixturefactory.Rule;
import lombok.NoArgsConstructor;
import works.weave.socks.orders.entities.Item;

@NoArgsConstructor(access = PRIVATE)
public class ItemTemplate {

  public static final String ITEM_ID = "62f5521b0e00db6610aee1b5";

  public static void loadTemplates() {
    of(Item.class)
        .addTemplate(
            VALID_ITEM,
            new Rule() {
              {
                add("id", ITEM_ID);
                add("itemId", "3395a43e-2d88-40de-b95f-e00e1502085b");
                add("quantity", 3);
                add("unitPrice", 18.0f);
              }
            })
        .addTemplate(INVALID_ITEM)
        .inherits(
            VALID_ITEM,
            new Rule() {
              {
                add("quantity", 6);
              }
            });
  }
}
