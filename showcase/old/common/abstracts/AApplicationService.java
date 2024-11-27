package heimdall.common.abstracts;

public interface AApplicationService<T> {
  public void handle(T event);
}
