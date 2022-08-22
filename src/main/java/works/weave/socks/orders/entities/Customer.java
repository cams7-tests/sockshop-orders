package works.weave.socks.orders.entities;

import static org.springframework.data.annotation.AccessType.Type.PROPERTY;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.AccessType;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
public class Customer extends RepresentationModel<Customer> {

  private static final String LINKS_PROPERTY = "_links";

  private String id;
  private String firstName;
  private String lastName;
  private String username;
  private List<Address> addresses;
  private List<Card> cards;

  @AccessType(PROPERTY)
  public void setLinks(List<Link> links) {
    super.removeLinks();
    super.add(links);
  }

  @JsonProperty(LINKS_PROPERTY)
  @Override
  public Links getLinks() {
    return super.getLinks();
  }
}
