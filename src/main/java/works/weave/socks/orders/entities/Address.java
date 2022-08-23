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
public class Address extends RepresentationModel<Address> {

  private static final String LINKS_PROPERTY = "_links";

  @Schema(example = "57a98d98e4b00679b4a830b0", description = "Address id")
  private String id;

  @Schema(example = "246", description = "Address number")
  private String number;

  @Schema(example = "Whitelees Road", description = "Address street")
  private String street;

  @Schema(example = "Glasgow", description = "Address city")
  private String city;

  @Schema(example = "G67 3DL", description = "Address post code")
  private String postcode;

  @Schema(example = "United Kingdom", description = "Address country")
  private String country;

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
