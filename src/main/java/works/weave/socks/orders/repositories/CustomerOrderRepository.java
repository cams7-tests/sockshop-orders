package works.weave.socks.orders.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import works.weave.socks.orders.entities.CustomerOrder;

@RepositoryRestResource // (path = "orders", itemResourceRel = "order")
public interface CustomerOrderRepository extends MongoRepository<CustomerOrder, String> {
  //  @RestResource(path = "customerId")
  //  List<CustomerOrder> findByCustomerId(@Param("custId") String id);
}
