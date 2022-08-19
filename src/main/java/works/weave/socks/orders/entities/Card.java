package works.weave.socks.orders.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
@Document
public class Card extends RepresentationModel<Card> {

  @Id private String id;
  private String longNum;
  private String expires;
  private String ccv;
}
