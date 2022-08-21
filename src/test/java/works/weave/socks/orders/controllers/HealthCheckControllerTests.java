package works.weave.socks.orders.controllers;

import static br.com.six2six.fixturefactory.Fixture.from;
import static org.apache.commons.lang3.ClassUtils.getPackageName;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static works.weave.socks.orders.template.DomainTemplateLoader.GET_APP_HEALTH;
import static works.weave.socks.orders.template.DomainTemplateLoader.GET_DB_HEALTH;
import static works.weave.socks.orders.template.DomainTemplateLoader.GET_DB_HEALTH_WITH_ERROR;

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.servlet.MockMvc;
import works.weave.socks.orders.entities.HealthCheck;
import works.weave.socks.orders.template.DomainTemplateLoader;

@WebMvcTest(controllers = HealthCheckController.class)
public class HealthCheckControllerTests {

  private static final String COMMAND = "{ buildInfo: 1 }";

  @Autowired private MockMvc mockMvc;

  @MockBean private MongoTemplate mongoTemplate;

  @BeforeAll
  static void loadTemplates() {
    FixtureFactoryLoader.loadTemplates(getPackageName(DomainTemplateLoader.class));
  }

  @Test
  void whenGetHealth_thenReturns200() throws Exception {
    HealthCheck appHealth = from(HealthCheck.class).gimme(GET_APP_HEALTH);
    HealthCheck dbHealth = from(HealthCheck.class).gimme(GET_DB_HEALTH);

    given(mongoTemplate.executeCommand(anyString())).willReturn(new Document());

    mockMvc
        .perform(get("/health"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.health[0].service", is(appHealth.getService())))
        .andExpect(jsonPath("$.health[0].status", is(appHealth.getStatus())))
        .andExpect(jsonPath("$.health[0].date", notNullValue()))
        .andExpect(jsonPath("$.health[1].service", is(dbHealth.getService())))
        .andExpect(jsonPath("$.health[1].status", is(dbHealth.getStatus())))
        .andExpect(jsonPath("$.health[1].date", notNullValue()));

    then(mongoTemplate).should(times(1)).executeCommand(eq(COMMAND));
  }

  @Test
  void whenGetHealthWithError_thenReturns200() throws Exception {
    HealthCheck appHealth = from(HealthCheck.class).gimme(GET_APP_HEALTH);
    HealthCheck dbHealth = from(HealthCheck.class).gimme(GET_DB_HEALTH_WITH_ERROR);

    given(mongoTemplate.executeCommand(anyString())).willThrow(RuntimeException.class);

    mockMvc
        .perform(get("/health"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.health[0].service", is(appHealth.getService())))
        .andExpect(jsonPath("$.health[0].status", is(appHealth.getStatus())))
        .andExpect(jsonPath("$.health[0].date", notNullValue()))
        .andExpect(jsonPath("$.health[1].service", is(dbHealth.getService())))
        .andExpect(jsonPath("$.health[1].status", is(dbHealth.getStatus())))
        .andExpect(jsonPath("$.health[1].date", notNullValue()));

    then(mongoTemplate).should(times(1)).executeCommand(eq(COMMAND));
  }
}
