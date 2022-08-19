package works.weave.socks.orders.services;

import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.RequestEntity.get;
import static org.springframework.http.RequestEntity.post;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Log4j2
@RequiredArgsConstructor
@Service
public class AsyncGetService {

  private final RestTemplate restTemplate;

  @Async
  public <T> Future<T> getResource(URI url, Class<T> type) {
    var request = get(url).accept(HAL_JSON).build();
    log.debug("Requesting: {}", request);
    var body = restTemplate.exchange(request, type).getBody();
    log.debug("Received: {}", body);
    return new AsyncResult<>(body);
  }

  @Async
  public <T> Future<List<T>> getDataList(URI url, ParameterizedTypeReference<List<T>> type)
      throws InterruptedException, IOException {
    var request = get(url).accept(APPLICATION_JSON).build();
    log.debug("Requesting: {}", request);
    var body = restTemplate.exchange(request, type).getBody();
    log.debug("Received: {}", body);
    return new AsyncResult<>(body);
  }

  @Async
  public <T, B> Future<T> postResource(URI uri, B body, ParameterizedTypeReference<T> returnType) {
    var request = post(uri).contentType(APPLICATION_JSON).accept(APPLICATION_JSON).body(body);
    log.debug("Requesting: {}", request);
    var responseBody = restTemplate.exchange(request, returnType).getBody();
    log.debug("Received: {}", responseBody);
    return new AsyncResult<>(responseBody);
  }
}
