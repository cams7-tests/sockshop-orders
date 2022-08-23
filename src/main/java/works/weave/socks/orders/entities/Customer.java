package works.weave.socks.orders.entities;

import static org.springframework.data.annotation.AccessType.Type.PROPERTY;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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

  @Schema(example = "57a98d98e4b00679b4a830b2", description = "Customer id")
  private String id;

  @Schema(example = "John", description = "Customer first name")
  private String firstName;

  @Schema(example = "Doe", description = "Customer last name")
  private String lastName;

  @Schema(example = "john", description = "Customer username")
  private String username;

  @Schema(description = "Customer addresses")
  private List<Address> addresses;

  @Schema(description = "Customer cards")
  private List<Card> cards;

  @AccessType(PROPERTY)
  public void setLinks(List<Link> links) {
    super.removeLinks();
    super.add(links);
  }

  @Schema(hidden = true)
  @JsonProperty(LINKS_PROPERTY)
  @Override
  public Links getLinks() {
    return super.getLinks();
  }
}
