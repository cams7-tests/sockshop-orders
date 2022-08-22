package works.weave.socks.orders.repositories;

import java.util.Collection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import works.weave.socks.orders.entities.CustomerOrder;

@Repository
public interface CustomerOrderRepository extends MongoRepository<CustomerOrder, String> {
  Collection<CustomerOrder> findByCustomerIdOrderByDateAsc(String customerId);
}
