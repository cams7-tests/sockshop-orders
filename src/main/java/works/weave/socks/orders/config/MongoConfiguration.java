package works.weave.socks.orders.config;

import com.mongodb.MongoClientOptions;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore(MongoAutoConfiguration.class)
public class MongoConfiguration {

  @Bean
  public MongoClientOptions optionsProvider() {
    MongoClientOptions.Builder optionsBuilder = new MongoClientOptions.Builder();
    optionsBuilder.serverSelectionTimeout(10000);
    MongoClientOptions options = optionsBuilder.build();
    return options;
  }
}
