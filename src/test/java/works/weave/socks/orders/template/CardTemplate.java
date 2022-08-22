package works.weave.socks.orders.template;

import static br.com.six2six.fixturefactory.Fixture.of;
import static lombok.AccessLevel.PRIVATE;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_CARD;

import br.com.six2six.fixturefactory.Rule;
import lombok.NoArgsConstructor;
import works.weave.socks.orders.entities.Card;

@NoArgsConstructor(access = PRIVATE)
public class CardTemplate {

  public static final String CARD_ID = "57a98d98e4b00679b4a830b1";

  public static final String GET_CARD_URL = String.format("http://user/cards/%s", CARD_ID);

  public static void loadTemplates() {
    of(Card.class)
        .addTemplate(
            VALID_CARD,
            new Rule() {
              {
                add("id", CARD_ID);
                add("longNum", "5544154011345918");
                add("expires", "08/19");
                add("ccv", "958");
              }
            });
  }
}
