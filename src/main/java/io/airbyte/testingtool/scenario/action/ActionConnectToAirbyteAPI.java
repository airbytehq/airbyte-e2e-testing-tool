package io.airbyte.testingtool.scenario.action;

import io.airbyte.api.client.AirbyteApiClient;
import io.airbyte.api.client.invoker.generated.ApiClient;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionConnectToAirbyteAPI extends ScenarioAction {
  private static final Logger LOGGER = LoggerFactory.getLogger(ActionConnectToAirbyteAPI.class);

  private static final String API_USER = "cloud-api";
  private static final String API_HOST_NODE = "apiHost";
  private static final String API_PORT_NODE = "apiPort";
  private static final String API_PATH_NODE = "apiPath";
  private static final String API_SCHEME_NODE = "apiScheme";

  private final AirbyteInstance airbyteInstance;

  @Builder
  public ActionConnectToAirbyteAPI(int order, AirbyteInstance airbyteInstance) {
    super(order);
    this.airbyteInstance = airbyteInstance;
  }

  @Override
  public String getActionName() {
    return "Connect to Airbyte API";
  }

  @Override
  public void doActionInternal() {
    connectToApi();
  }

  private void connectToApi() {
    var creds = airbyteInstance.getCredentialConfig();
    String apiServerHost = creds.getCredentialJson().get(API_HOST_NODE).textValue();
    int apiServerPort = Integer.valueOf(creds.getCredentialJson().get(API_PORT_NODE).textValue());
    String apiPath = creds.getCredentialJson().get(API_PATH_NODE).textValue();
    apiPath = apiPath == null ? "/api" : apiPath;
    String apiScheme = creds.getCredentialJson().get(API_SCHEME_NODE).textValue();
    apiScheme = apiScheme == null ? "http" : apiScheme;

    LOGGER.info("Creating Airbyte Config Api Client");
    AirbyteApiClient airbyteApi = new AirbyteApiClient(new ApiClient()
        .setScheme(apiScheme)
        .setHost(apiServerHost)
        .setPort(apiServerPort)
        .setBasePath(apiPath)
        .setRequestInterceptor(builder -> {
          builder.setHeader("X-Endpoint-API-UserInfo", getAuthHeader(API_USER, true));
          builder.setHeader("User-Agent", "Airbyte-E2E-Testing-Tool");
        }));

    airbyteInstance.setAirbyteApi(airbyteApi);
  }

  private static String getAuthHeader(String userId, final boolean verified) {
    final var payload = ("{\"user_id\": \"" + userId + "\", \"email_verified\":" + verified + "}").getBytes(StandardCharsets.UTF_8);
    return Base64.getEncoder().encodeToString(payload);
  }
}
