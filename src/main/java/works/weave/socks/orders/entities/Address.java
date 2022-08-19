package works.weave.socks.orders.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
@Document
public class Address extends RepresentationModel<Address> {

  @Id private String id;
  private String number;
  private String street;
  private String city;
  private String postcode;
  private String country;
}
