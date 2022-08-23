package works.weave.socks.orders.controllers;

import static org.springframework.http.HttpStatus.OK;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import works.weave.socks.orders.entities.HealthCheck;

@Tag(name = "Health Check Service")
@RequiredArgsConstructor
@RestController
public class HealthCheckController {

  private static final String STATUS_OK = "OK";

  private final MongoTemplate mongoTemplate;

  @Operation(description = "Get health")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Ok")})
  @ResponseStatus(OK)
  @GetMapping(path = "/health")
  @ResponseBody
  Map<String, List<HealthCheck>> getHealth() {
    var map = new HashMap<String, List<HealthCheck>>();
    var healthChecks = new ArrayList<HealthCheck>();

    var app = new HealthCheck("orders", STATUS_OK);
    var database = new HealthCheck("orders-db", STATUS_OK);

    try {
      mongoTemplate.executeCommand("{ buildInfo: 1 }");
    } catch (RuntimeException e) {
      database.setStatus("err");
    }

    healthChecks.add(app);
    healthChecks.add(database);

    map.put("health", healthChecks);
    return map;
  }
}
