package works.weave.socks.orders.template;

import br.com.six2six.fixturefactory.loader.TemplateLoader;

public class DomainTemplateLoader implements TemplateLoader {

  public static final String VALID_NEW_ORDER_RESOURCE = "VALID_NEW_ORDER_RESOURCE";
  public static final String INVALID_NEW_ORDER_RESOURCE = "INVALID_NEW_ORDER_RESOURCE";
  public static final String VALID_ADDRESS = "VALID_ADDRESS";
  public static final String VALID_CARD = "VALID_CARD";
  public static final String VALID_CUSTOMER = "VALID_CUSTOMER";
  public static final String VALID_ITEM = "VALID_ITEM";
  public static final String INVALID_ITEM = "INVALID_ITEM";
  public static final String VALID_PAYMENT = "VALID_PAYMENT";
  public static final String INVALID_PAYMENT = "INVALID_PAYMENT";
  public static final String SHIPMENT = "SHIPMENT";

  @Override
  public void load() {
    NewOrderResourceTemplate.loadTemplates();
    AddressTemplate.loadTemplates();
    CardTemplate.loadTemplates();
    CustomerTemplate.loadTemplates();
    ItemTemplate.loadTemplates();
    PaymentResponseTemplate.loadTemplates();
    ShipmentTemplate.loadTemplates();
  }
}
