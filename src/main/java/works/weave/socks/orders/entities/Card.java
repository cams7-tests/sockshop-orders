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
public class Card extends RepresentationModel<Card> {

  private static final String LINKS_PROPERTY = "_links";

  private String id;
  private String longNum;
  private String expires;
  private String ccv;

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
