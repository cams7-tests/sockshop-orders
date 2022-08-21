package works.weave.socks.orders.entities;

import lombok.Data;

@Data
public class Item {
  private String id;
  private String itemId;
  private int quantity;
  private float unitPrice;
}
