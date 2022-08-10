package works.weave.socks.orders.repositories;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import works.weave.socks.orders.entities.CustomerOrder;

@RepositoryRestResource(path = "orders", itemResourceRel = "order")
public interface CustomerOrderRepository extends MongoRepository<CustomerOrder, String> {
  @RestResource(path = "customerId")
  List<CustomerOrder> findByCustomerId(@Param("custId") String id);
}
