package io.airbyte.testingtool.scenario.action.airbyte;

import io.airbyte.api.client.AirbyteApiClient;
import io.airbyte.api.client.invoker.generated.ApiClient;
import io.airbyte.testingtool.scenario.action.ScenarioAction;
import io.airbyte.testingtool.scenario.instance.AirbyteApiInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
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

  private final AirbyteApiInstance airbyteApiInstance;
  private String url;

  @Builder
  public ActionConnectToAirbyteAPI(int order, List<Instance> requiredInstances, Instance resultInstance, AirbyteApiInstance airbyteApiInstance) {
    super(order, requiredInstances, resultInstance);
    this.airbyteApiInstance = airbyteApiInstance;
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
    var creds = airbyteApiInstance.getCredentialConfig();
    String apiServerHost = creds.getCredentialJson().get(API_HOST_NODE).textValue();
    var apiServerPortJson = creds.getCredentialJson().get(API_PORT_NODE);
    var apiServerPort = apiServerPortJson == null ? 80 : Integer.parseInt(apiServerPortJson.textValue());
    var apiPathJson = creds.getCredentialJson().get(API_PATH_NODE);
    var apiPath = apiPathJson == null ? "/api" : apiPathJson.textValue();
    var apiSchemeJson = creds.getCredentialJson().get(API_SCHEME_NODE);
    var apiScheme = apiSchemeJson == null ? "http" : apiSchemeJson.textValue();
    url = apiScheme + "://" + apiServerHost + ":" + apiServerPort;

    LOGGER.info("Creating Airbyte Config Api Client...");
    AirbyteApiClient airbyteApi = new AirbyteApiClient(new ApiClient()
        .setScheme(apiScheme)
        .setHost(apiServerHost)
        .setPort(apiServerPort)
        .setBasePath(apiPath)
        .setRequestInterceptor(builder -> {
          builder.setHeader("X-Endpoint-API-UserInfo", getAuthHeader(API_USER, true));
          builder.setHeader("User-Agent", "Airbyte-E2E-Testing-Tool");
        }));

    airbyteApiInstance.setAirbyteApi(airbyteApi);
  }

  @Override
  protected String getContextInternal() {
    return "Instance name : **" + airbyteApiInstance.getInstanceName() + "**. Url : " + url;
  }

  private static String getAuthHeader(String userId, final boolean verified) {
    final var payload = ("{\"user_id\": \"" + userId + "\", \"email_verified\":" + verified + "}").getBytes(StandardCharsets.UTF_8);
    return Base64.getEncoder().encodeToString(payload);
  }
}
