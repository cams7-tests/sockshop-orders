package works.weave.socks.orders.controllers;

import static br.com.six2six.fixturefactory.Fixture.from;
import static org.apache.commons.lang3.ClassUtils.getPackageName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static works.weave.socks.orders.entities.CustomerOrder.COLLECTION_NAME;
import static works.weave.socks.orders.template.DomainTemplateLoader.INVALID_ITEM;
import static works.weave.socks.orders.template.DomainTemplateLoader.INVALID_NEW_ORDER_RESOURCE;
import static works.weave.socks.orders.template.DomainTemplateLoader.INVALID_PAYMENT;
import static works.weave.socks.orders.template.DomainTemplateLoader.SHIPMENT;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_ADDRESS;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_CARD;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_CUSTOMER;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_ITEM;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_NEW_ORDER_RESOURCE;
import static works.weave.socks.orders.template.DomainTemplateLoader.VALID_PAYMENT;
import static works.weave.socks.orders.template.NewOrderResourceTemplate.GET_CUSTOMER_URL;
import static works.weave.socks.orders.template.NewOrderResourceTemplate.INVALID_CUSTOMER_URL;

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.hateoas.Link;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import works.weave.socks.orders.config.OrdersConfigurationProperties;
import works.weave.socks.orders.entities.Address;
import works.weave.socks.orders.entities.Card;
import works.weave.socks.orders.entities.Customer;
import works.weave.socks.orders.entities.Item;
import works.weave.socks.orders.entities.Shipment;
import works.weave.socks.orders.resources.NewOrderResource;
import works.weave.socks.orders.services.AsyncGetService;
import works.weave.socks.orders.template.DomainTemplateLoader;
import works.weave.socks.orders.values.PaymentRequest;
import works.weave.socks.orders.values.PaymentResponse;

@SpringBootTest
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
public class OrdersControllerTests {

  private static final String SELF_LINK = "self";
  private static final float SHIPPING = 4.99F;

  private static final URI PAYMENT_URL = URI.create("http://payment/paymentAuth");
  private static final URI SHIPPING_URL = URI.create("http://shipping/shipping");

  @MockBean private OrdersConfigurationProperties config;

  @MockBean private AsyncGetService asyncGetService;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private WebApplicationContext applicationContext;

  @Autowired private MongoTemplate mongoTemplate;

  private MockMvc mockMvc;

  @Captor private ArgumentCaptor<PaymentRequest> paymentRequestCaptor;
  @Captor private ArgumentCaptor<Shipment> shipmentCaptor;

  @BeforeAll
  static void loadTemplates() {
    FixtureFactoryLoader.loadTemplates(getPackageName(DomainTemplateLoader.class));
  }

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
  }

  @Test
  void whenPaymentAuthorised_thenReturns200() throws Exception {
    NewOrderResource newOrderResource =
        from(NewOrderResource.class).gimme(VALID_NEW_ORDER_RESOURCE);
    Address address = from(Address.class).gimme(VALID_ADDRESS);
    Card card = from(Card.class).gimme(VALID_CARD);
    Customer customer = from(Customer.class).gimme(VALID_CUSTOMER);
    customer.add(Link.of(GET_CUSTOMER_URL, SELF_LINK));
    Item item = from(Item.class).gimme(VALID_ITEM);
    PaymentResponse paymentResponse = from(PaymentResponse.class).gimme(VALID_PAYMENT);
    Shipment shipment = from(Shipment.class).gimme(SHIPMENT);

    given(config.getPaymentUri()).willReturn(PAYMENT_URL);
    given(config.getShippingUri()).willReturn(SHIPPING_URL);

    given(asyncGetService.getResource(eq(newOrderResource.getAddress()), any()))
        .willReturn(new AsyncResult<>(address));
    given(asyncGetService.getResource(eq(newOrderResource.getCard()), any()))
        .willReturn(new AsyncResult<>(card));
    given(asyncGetService.getResource(eq(newOrderResource.getCustomer()), any()))
        .willReturn(new AsyncResult<>(customer));
    given(asyncGetService.getDataList(eq(newOrderResource.getItems()), any()))
        .willReturn(new AsyncResult<>(List.of(item)));

    given(asyncGetService.postResource(eq(PAYMENT_URL), any(PaymentRequest.class), any()))
        .willReturn(new AsyncResult<>(paymentResponse));
    given(asyncGetService.postResource(eq(SHIPPING_URL), any(Shipment.class), any()))
        .willReturn(new AsyncResult<>(shipment));

    float amount = (item.getQuantity() * item.getUnitPrice()) + SHIPPING;

    mockMvc
        .perform(
            post("/orders")
                .content(objectMapper.writeValueAsString(newOrderResource))
                .contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.customerId", is(customer.getId())))
        .andExpect(jsonPath("$.customer.id", is(customer.getId())))
        .andExpect(jsonPath("$.address.id", is(address.getId())))
        .andExpect(jsonPath("$.card.id", is(card.getId())))
        .andExpect(jsonPath("$.shipment.name", is(customer.getId())))
        .andExpect(jsonPath("$.date", notNullValue()))
        .andExpect(jsonPath("$.total", notNullValue()));

    then(asyncGetService).should(times(1)).getResource(eq(newOrderResource.getAddress()), any());
    then(asyncGetService).should(times(1)).getResource(eq(newOrderResource.getCard()), any());
    then(asyncGetService).should(times(1)).getResource(eq(newOrderResource.getCustomer()), any());
    then(asyncGetService).should(times(1)).getDataList(eq(newOrderResource.getItems()), any());
    then(asyncGetService)
        .should(times(1))
        .postResource(eq(PAYMENT_URL), paymentRequestCaptor.capture(), any());
    then(asyncGetService)
        .should(times(1))
        .postResource(eq(SHIPPING_URL), shipmentCaptor.capture(), any());

    assertThat(paymentRequestCaptor.getValue())
        .isEqualTo(getPaymentRequest(address, card, customer, amount));
    assertThat(shipmentCaptor.getValue().getName()).isEqualTo(shipment.getName());

    assertThat(
            mongoTemplate.exists(
                new Query().addCriteria(Criteria.where("customerId").is(customer.getId())),
                COLLECTION_NAME))
        .isTrue();
    mongoTemplate.dropCollection(COLLECTION_NAME);
  }

  @Test
  void whenPaymentDeclined_thenReturns406() throws Exception {
    NewOrderResource newOrderResource =
        from(NewOrderResource.class).gimme(VALID_NEW_ORDER_RESOURCE);
    Address address = from(Address.class).gimme(VALID_ADDRESS);
    Card card = from(Card.class).gimme(VALID_CARD);
    Customer customer = from(Customer.class).gimme(VALID_CUSTOMER);
    customer.add(Link.of(GET_CUSTOMER_URL, SELF_LINK));
    Item item = from(Item.class).gimme(INVALID_ITEM);
    PaymentResponse paymentResponse = from(PaymentResponse.class).gimme(INVALID_PAYMENT);

    given(config.getPaymentUri()).willReturn(PAYMENT_URL);
    given(config.getShippingUri()).willReturn(SHIPPING_URL);

    given(asyncGetService.getResource(eq(newOrderResource.getAddress()), any()))
        .willReturn(new AsyncResult<>(address));
    given(asyncGetService.getResource(eq(newOrderResource.getCard()), any()))
        .willReturn(new AsyncResult<>(card));
    given(asyncGetService.getResource(eq(newOrderResource.getCustomer()), any()))
        .willReturn(new AsyncResult<>(customer));
    given(asyncGetService.getDataList(eq(newOrderResource.getItems()), any()))
        .willReturn(new AsyncResult<>(List.of(item)));

    given(asyncGetService.postResource(eq(PAYMENT_URL), any(PaymentRequest.class), any()))
        .willReturn(new AsyncResult<>(paymentResponse));
    then(asyncGetService).should(never()).postResource(eq(SHIPPING_URL), any(), any());

    mockMvc
        .perform(
            post("/orders")
                .content(objectMapper.writeValueAsString(newOrderResource))
                .contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().is4xxClientError());

    then(asyncGetService).should(times(1)).getResource(eq(newOrderResource.getAddress()), any());
    then(asyncGetService).should(times(1)).getResource(eq(newOrderResource.getCard()), any());
    then(asyncGetService).should(times(1)).getResource(eq(newOrderResource.getCustomer()), any());
    then(asyncGetService).should(times(1)).getDataList(eq(newOrderResource.getItems()), any());
    then(asyncGetService)
        .should(times(1))
        .postResource(eq(PAYMENT_URL), paymentRequestCaptor.capture(), any());

    assertThat(paymentRequestCaptor.getValue())
        .isEqualTo(
            getPaymentRequest(
                address, card, customer, (item.getQuantity() * item.getUnitPrice()) + SHIPPING));

    assertThat(
            mongoTemplate.exists(
                new Query().addCriteria(Criteria.where("customerId").is(customer.getId())),
                COLLECTION_NAME))
        .isFalse();
  }

  @Test
  void whenPassInvalidOrderRequest_thenReturns406() throws Exception {
    var newOrderResource = new NewOrderResource();

    given(config.getPaymentUri()).willReturn(PAYMENT_URL);
    given(config.getShippingUri()).willReturn(SHIPPING_URL);

    mockMvc
        .perform(
            post("/orders")
                .content(objectMapper.writeValueAsString(newOrderResource))
                .contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().is4xxClientError());

    then(asyncGetService).should(never()).getResource(any(), any());
    then(asyncGetService).should(never()).getDataList(any(), any());
    then(asyncGetService).should(never()).postResource(any(), any(), any());
  }

  @Test
  void whenPassInvalidCustomerUrl_thenReturns406() throws Exception {
    NewOrderResource newOrderResource =
        from(NewOrderResource.class).gimme(INVALID_NEW_ORDER_RESOURCE);
    Address address = from(Address.class).gimme(VALID_ADDRESS);
    Card card = from(Card.class).gimme(VALID_CARD);
    Customer customer = from(Customer.class).gimme(VALID_CUSTOMER);
    customer.add(Link.of(INVALID_CUSTOMER_URL, SELF_LINK));
    Item item = from(Item.class).gimme(VALID_ITEM);
    PaymentResponse paymentResponse = from(PaymentResponse.class).gimme(VALID_PAYMENT);

    given(config.getPaymentUri()).willReturn(PAYMENT_URL);
    given(config.getShippingUri()).willReturn(SHIPPING_URL);

    given(asyncGetService.getResource(eq(newOrderResource.getAddress()), any()))
        .willReturn(new AsyncResult<>(address));
    given(asyncGetService.getResource(eq(newOrderResource.getCard()), any()))
        .willReturn(new AsyncResult<>(card));
    given(asyncGetService.getResource(eq(newOrderResource.getCustomer()), any()))
        .willReturn(new AsyncResult<>(customer));
    given(asyncGetService.getDataList(eq(newOrderResource.getItems()), any()))
        .willReturn(new AsyncResult<>(List.of(item)));

    given(asyncGetService.postResource(eq(PAYMENT_URL), any(PaymentRequest.class), any()))
        .willReturn(new AsyncResult<>(paymentResponse));

    float amount = (item.getQuantity() * item.getUnitPrice()) + SHIPPING;

    mockMvc
        .perform(
            post("/orders")
                .content(objectMapper.writeValueAsString(newOrderResource))
                .contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().is4xxClientError());

    then(asyncGetService).should(times(1)).getResource(eq(newOrderResource.getAddress()), any());
    then(asyncGetService).should(times(1)).getResource(eq(newOrderResource.getCard()), any());
    then(asyncGetService).should(times(1)).getResource(eq(newOrderResource.getCustomer()), any());
    then(asyncGetService).should(times(1)).getDataList(eq(newOrderResource.getItems()), any());
    then(asyncGetService)
        .should(times(1))
        .postResource(eq(PAYMENT_URL), paymentRequestCaptor.capture(), any());
    then(asyncGetService).should(never()).postResource(eq(SHIPPING_URL), any(), any());

    assertThat(paymentRequestCaptor.getValue())
        .isEqualTo(getPaymentRequest(address, card, customer, amount));

    assertThat(
            mongoTemplate.exists(
                new Query().addCriteria(Criteria.where("customerId").is(customer.getId())),
                COLLECTION_NAME))
        .isFalse();
  }

  @Test
  void whenThrowsExecutionException_thenReturns500() throws Exception {
    NewOrderResource newOrderResource =
        from(NewOrderResource.class).gimme(VALID_NEW_ORDER_RESOURCE);

    given(config.getPaymentUri()).willReturn(PAYMENT_URL);
    given(config.getShippingUri()).willReturn(SHIPPING_URL);

    given(asyncGetService.getResource(any(), any())).willReturn(new AsyncResultFake<>());

    mockMvc
        .perform(
            post("/orders")
                .content(objectMapper.writeValueAsString(newOrderResource))
                .contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().is5xxServerError());

    then(asyncGetService).should(times(1)).getResource(eq(newOrderResource.getAddress()), any());
    then(asyncGetService).should(times(1)).getResource(eq(newOrderResource.getCard()), any());
    then(asyncGetService).should(times(1)).getResource(eq(newOrderResource.getCustomer()), any());
    then(asyncGetService).should(times(1)).getDataList(eq(newOrderResource.getItems()), any());
    then(asyncGetService).should(never()).postResource(eq(PAYMENT_URL), any(), any());
    then(asyncGetService).should(never()).postResource(eq(SHIPPING_URL), any(), any());
  }

  private static PaymentRequest getPaymentRequest(
      Address address, Card card, Customer customer, float amount) {
    return new PaymentRequest(address, card, customer, amount);
  }

  private static class AsyncResultFake<T> extends AsyncResult<T> {
    public AsyncResultFake(T value) {
      super(value);
    }

    public AsyncResultFake() {
      this(null);
    }

    @Override
    public T get() throws ExecutionException {
      throw new ExecutionException("Error", null);
    }
  }
}
