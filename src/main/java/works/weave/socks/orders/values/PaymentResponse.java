package works.weave.socks.orders.values;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentResponse {
  private boolean authorised = false;
  private String message;

  public PaymentResponse(boolean authorised, String message) {
    this();
    this.authorised = authorised;
    this.message = message;
  }
}
