package works.weave.socks.orders.template;

import static br.com.six2six.fixturefactory.Fixture.of;
import static lombok.AccessLevel.PRIVATE;
import static works.weave.socks.orders.template.DomainTemplateLoader.INVALID_PAYMENT;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_PAYMENT;

import br.com.six2six.fixturefactory.Rule;
import lombok.NoArgsConstructor;
import works.weave.socks.orders.values.PaymentResponse;

@NoArgsConstructor(access = PRIVATE)
public class PaymentResponseTemplate {

  public static void loadTemplates() {
    of(PaymentResponse.class)
        .addTemplate(
            VALID_PAYMENT,
            new Rule() {
              {
                add("authorised", true);
                add("message", "Payment authorised");
              }
            })
        .addTemplate(
            INVALID_PAYMENT,
            new Rule() {
              {
                add("authorised", false);
                add("message", "Payment declined: amount exceeds 100.00");
              }
            });
  }
}
