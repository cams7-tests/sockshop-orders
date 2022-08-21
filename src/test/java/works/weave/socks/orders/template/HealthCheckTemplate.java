package works.weave.socks.orders.template;

import static br.com.six2six.fixturefactory.Fixture.of;
import static java.time.LocalDateTime.parse;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static lombok.AccessLevel.PRIVATE;
import static works.weave.socks.orders.template.DomainTemplateLoader.GET_APP_HEALTH;
import static works.weave.socks.orders.template.DomainTemplateLoader.GET_DB_HEALTH;
import static works.weave.socks.orders.template.DomainTemplateLoader.GET_DB_HEALTH_WITH_ERROR;

import br.com.six2six.fixturefactory.Rule;
import lombok.NoArgsConstructor;
import works.weave.socks.orders.entities.HealthCheck;

@NoArgsConstructor(access = PRIVATE)
public class HealthCheckTemplate {

  public static void loadTemplates() {
    of(HealthCheck.class)
        .addTemplate(
            GET_APP_HEALTH,
            new Rule() {
              {
                add("service", "orders");
                add("status", "OK");
                add("date", parse("2022-08-20T18:29:40.903361", ISO_DATE_TIME));
              }
            })
        .addTemplate(GET_DB_HEALTH)
        .inherits(
            GET_APP_HEALTH,
            new Rule() {
              {
                add("service", "orders-db");
              }
            })
        .addTemplate(GET_DB_HEALTH_WITH_ERROR)
        .inherits(
            GET_DB_HEALTH,
            new Rule() {
              {
                add("status", "err");
              }
            });
  }
}
