package works.weave.socks.orders.controllers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.util.CollectionUtils.isEmpty;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import works.weave.socks.orders.config.OrdersConfigurationProperties;
import works.weave.socks.orders.entities.Address;
import works.weave.socks.orders.entities.Card;
import works.weave.socks.orders.entities.Customer;
import works.weave.socks.orders.entities.CustomerOrder;
import works.weave.socks.orders.entities.Item;
import works.weave.socks.orders.entities.Shipment;
import works.weave.socks.orders.repositories.CustomerOrderRepository;
import works.weave.socks.orders.resources.NewOrderResource;
import works.weave.socks.orders.services.AsyncGetService;
import works.weave.socks.orders.values.PaymentRequest;
import works.weave.socks.orders.values.PaymentResponse;

@Tag(name = "Customer Order Service")
@RequiredArgsConstructor
@Log4j2
@RestController
@RequestMapping(path = "/orders", produces = APPLICATION_JSON_VALUE)
public class OrdersController {

  private static final String SELF_LINK = "self";
  private static final String ORDER_LINK = "order";
  private static final float SHIPPING = 4.99f;

  private final OrdersConfigurationProperties config;
  private final AsyncGetService asyncGetService;
  private final CustomerOrderRepository customerOrderRepository;

  @Value(value = "${http.timeout:5}")
  private long timeout;

  @Operation(description = "Create an customer order")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Created"),
    @ApiResponse(
        responseCode = "406",
        description = "Not Acceptable",
        content = @Content(schema = @Schema(hidden = true))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(schema = @Schema(hidden = true)))
  })
  @ResponseStatus(CREATED)
  @PostMapping(consumes = APPLICATION_JSON_VALUE)
  CustomerOrder newOrder(@RequestBody NewOrderResource item) {
    try {
      if (item.getAddress() == null
          || item.getCustomer() == null
          || item.getCard() == null
          || item.getItems() == null) {
        throw new ResponseStatusException(
            NOT_ACCEPTABLE,
            "Invalid order request. Order requires customer, address, card and items.");
      }

      log.debug("Starting calls");
      var addressFuture = asyncGetService.getResource(item.getAddress(), Address.class);
      var customerFuture = asyncGetService.getResource(item.getCustomer(), Customer.class);
      var cardFuture = asyncGetService.getResource(item.getCard(), Card.class);
      var itemsFuture =
          asyncGetService.getDataList(
              item.getItems(), new ParameterizedTypeReference<List<Item>>() {});
      log.debug("End of calls.");

      var address = addressFuture.get(timeout, SECONDS);
      var customer = customerFuture.get(timeout, SECONDS);
      var card = cardFuture.get(timeout, SECONDS);
      var items = itemsFuture.get(timeout, SECONDS);

      var amount = calculateTotal(items);

      // Call payment service to make sure they've paid
      var paymentRequest = new PaymentRequest(address, card, customer, amount);
      log.info("Sending payment request: {}", paymentRequest);
      var paymentFuture =
          asyncGetService.postResource(
              config.getPaymentUri(),
              paymentRequest,
              new ParameterizedTypeReference<PaymentResponse>() {});

      var payment = paymentFuture.get(timeout, SECONDS);
      log.info("Received payment response: {}", payment);

      if (payment == null) {
        throw new ResponseStatusException(
            INTERNAL_SERVER_ERROR, "Unable to parse authorisation packet");
      }
      if (!payment.isAuthorised()) {
        throw new ResponseStatusException(NOT_ACCEPTABLE, payment.getMessage());
      }

      // Ship
      var customerId = parseId(customer.getLink(SELF_LINK).get().getHref());
      var shipmentFuture =
          asyncGetService.postResource(
              config.getShippingUri(),
              new Shipment(customerId),
              new ParameterizedTypeReference<Shipment>() {});

      var shipment = shipmentFuture.get(timeout, SECONDS);

      var customerOrder =
          new CustomerOrder(
              customerId,
              customer.removeLinks(),
              address.removeLinks(),
              card.removeLinks(),
              items,
              shipment,
              amount);
      log.debug("Received customerOrder: {}", customerOrder);

      var savedCustomerOrder = customerOrderRepository.save(customerOrder);
      log.debug("Saved customerOrder: {}", savedCustomerOrder);

      return savedCustomerOrder;
    } catch (TimeoutException e) {
      throw new ResponseStatusException(
          INTERNAL_SERVER_ERROR,
          "Unable to create order due to timeout from one of the services.",
          e);
    } catch (InterruptedException | IOException | ExecutionException e) {
      throw new ResponseStatusException(
          INTERNAL_SERVER_ERROR, "Unable to create order due to unspecified IO error.", e);
    }
  }

  @Operation(description = "Get an customer order by id")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Ok")})
  @ResponseStatus(OK)
  @GetMapping(value = "/{id}")
  CustomerOrder getOrder(
      @Parameter(name = "id", required = true, description = "Customer order id") @PathVariable
          String id) {
    var customerOrder = customerOrderRepository.findById(id).orElseThrow();
    addLink(customerOrder);
    return customerOrder;
  }

  @Operation(description = "Get customer orders by customer id")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Ok"),
    @ApiResponse(
        responseCode = "404",
        description = "Not Found",
        content = @Content(schema = @Schema(hidden = true)))
  })
  @ResponseStatus(OK)
  @GetMapping(value = "/search/customerId")
  List<CustomerOrder> getOrders(
      @Parameter(name = "custId", required = true, description = "Customer id")
          @RequestParam("custId")
          String customerId) {
    var orders = customerOrderRepository.findByCustomerIdOrderByDateAsc(customerId);

    if (isEmpty(orders)) {
      throw new ResponseStatusException(
          NOT_FOUND, String.format("No orders found for user: %s", customerId));
    }

    return orders.stream()
        .map(
            customerOrder -> {
              addLink(customerOrder);
              return customerOrder;
            })
        .collect(Collectors.toList());
  }

  private static String parseId(String href) {
    var idPattern = Pattern.compile("[\\w-]+$");
    var matcher = idPattern.matcher(href);
    if (!matcher.find()) {
      throw new ResponseStatusException(
          NOT_ACCEPTABLE, String.format("Could not parse user ID from: %s", href));
    }
    return matcher.group(0);
  }

  private static float calculateTotal(List<Item> items) {
    var amount = 0f;
    amount += items.stream().mapToDouble(item -> item.getQuantity() * item.getUnitPrice()).sum();
    amount += SHIPPING;
    return amount;
  }

  private static void addLink(CustomerOrder customerOrder) {
    var link = linkTo(methodOn(OrdersController.class).getOrder(customerOrder.getId()));
    customerOrder.add(link.withSelfRel());
    customerOrder.add(link.withRel(ORDER_LINK));
  }
}
