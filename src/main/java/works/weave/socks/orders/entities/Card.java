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
public class Card extends RepresentationModel<Card> {

  private static final String LINKS_PROPERTY = "_links";

  @Schema(example = "57a98d98e4b00679b4a830b1", description = "Card id")
  private String id;

  @Schema(example = "5544154011345918", description = "Card number")
  private String longNum;

  @Schema(example = "08/19", description = "Card expiration date")
  private String expires;

  @Schema(example = "958", description = "Card verification value")
  private String ccv;

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
