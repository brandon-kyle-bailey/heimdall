package heimdall.common.abstracts;

public interface AQuery<T> {
  public void handle(T event);
}
