package works.weave.socks.orders.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Item {
  @Schema(example = "62f5521b0e00db6610aee1b5", description = "Item id")
  private String id;

  @Schema(example = "3395a43e-2d88-40de-b95f-e00e1502085b", description = "Catalogue item id")
  private String itemId;

  @Schema(example = "3", description = "Item quantity")
  private int quantity;

  @Schema(example = "18.0", description = "Item unit price")
  private float unitPrice;
}
