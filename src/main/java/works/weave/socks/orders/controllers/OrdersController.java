package works.weave.socks.orders.controllers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
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

@RequiredArgsConstructor
@Log4j2
@RestController
public class OrdersController {

  private static final String SELF_LINK = "self";
  private static final float SHIPPING = 4.99F;

  private final OrdersConfigurationProperties config;
  private final AsyncGetService asyncGetService;
  private final CustomerOrderRepository customerOrderRepository;

  @Value(value = "${http.timeout:5}")
  private long timeout;

  @ResponseStatus(CREATED)
  @PostMapping(path = "/orders", consumes = APPLICATION_JSON_VALUE)
  @ResponseBody
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
          new CustomerOrder(customerId, customer, address, card, items, shipment, amount);
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

  private static String parseId(String href) {
    var idPattern = Pattern.compile("[\\w-]+$");
    var matcher = idPattern.matcher(href);
    if (!matcher.find()) {
      throw new ResponseStatusException(
          NOT_ACCEPTABLE, String.format("Could not parse user ID from: %s", href));
    }
    return matcher.group(0);
  }

  //    TODO: Add link to shipping
  //    @RequestMapping(method = RequestMethod.GET, value = "/orders")
  //    public @ResponseBody
  //    ResponseEntity<?> getOrders() {
  //        List<CustomerOrder> customerOrders = customerOrderRepository.findAll();
  //
  //        Resources<CustomerOrder> resources = new Resources<>(customerOrders);
  //
  //        resources.forEach(r -> r);
  //
  //        resources.add(linkTo(methodOn(ShippingController.class,
  // CustomerOrder.getShipment::ge)).withSelfRel());
  //
  //        // add other links as needed
  //
  //        return ResponseEntity.ok(resources);
  //    }

  private static float calculateTotal(List<Item> items) {
    var amount = 0F;
    amount += items.stream().mapToDouble(item -> item.getQuantity() * item.getUnitPrice()).sum();
    amount += SHIPPING;
    return amount;
  }
}
