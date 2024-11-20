package heimdall.adapters;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;

import common.shared.inter.HttpAdapterFetchConfig;

public class HttpAdapter {

  public static Object fetch(String url, HttpAdapterFetchConfig config) {
    try {
      HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(url));
      if (config.headers != null) {
        for (String key : config.headers.keySet()) {
          requestBuilder.header(key, config.headers.get(key));
        }
      }
      if (config.data != null) {
        requestBuilder.method(config.method.toString(),
            HttpRequest.BodyPublishers.ofString(new JSONObject(config.data).toString()));
      } else {
        requestBuilder.method(config.method.toString(), HttpRequest.BodyPublishers.noBody());
      }

      HttpRequest request = requestBuilder.build();
      System.out.println(request.toString());
      HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      Object result = response.body();
      System.out.println(result);
      return result;
    } catch (

    Exception e) {
      System.err.println(e);
    }
    return null;
  }
}
