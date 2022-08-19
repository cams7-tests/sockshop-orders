package works.weave.socks.orders.config;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class OrdersConfigurationProperties {

  @Value(value = "${apis.payment}")
  private String paymentUrl;

  @Value(value = "${apis.shipping}")
  private String shippingUrl;

  public URI getPaymentUri() {
    return new ServiceUri(paymentUrl, "/paymentAuth").toUri();
  }

  public URI getShippingUri() {
    return new ServiceUri(shippingUrl, "/shipping").toUri();
  }

  @RequiredArgsConstructor
  @ToString
  private class ServiceUri {
    private final String url;
    private final String endpoint;

    public URI toUri() {
      return URI.create(String.format("%s%s", url, endpoint));
    }
  }
}
