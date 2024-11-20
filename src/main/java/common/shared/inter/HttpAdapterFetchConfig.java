package common.shared.inter;

import java.util.Map;

import common.shared.enumerator.EHttpMethod;

public class HttpAdapterFetchConfig {
  public EHttpMethod method;
  public Map<String, String> headers;
  public Map<String, String> data;

  public HttpAdapterFetchConfig(EHttpMethod method, Map<String, String> headers, Map<String, String> data) {
    this.method = method;
    this.headers = headers;
    this.data = data;
  }

}
