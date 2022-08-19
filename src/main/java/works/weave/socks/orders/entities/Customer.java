package works.weave.socks.orders.entities;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
@Document
public class Customer extends RepresentationModel<Customer> {

  @Id private String id;
  private String firstName;
  private String lastName;
  private String username;
  private List<Address> addresses;
  private List<Card> cards;
}
