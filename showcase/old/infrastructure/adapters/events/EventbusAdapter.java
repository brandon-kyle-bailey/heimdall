package heimdall.infrastructure.adapters.events;

import java.util.HashMap;
import java.util.Map;

import heimdall.infrastructure.ports.logging.LoggingPort;

public class EventbusAdapter {

  private final LoggingPort logManager;

  private Map<String, Object> eventHandlers = new HashMap<>();

  public EventbusAdapter(LoggingPort logManager) {
    this.logManager = logManager;
  }

  public void subscribe(String eventType, Object handler) {
    eventHandlers.put(eventType, handler);
  }

  public void publish(String eventType, Object event) {
    Object handler = eventHandlers.get(eventType);

    if (handler != null) {
      try {
        // handle method defined in heimdall.common.intefaces.IEventHandler
        handler.getClass().getMethod("handle", event.getClass()).invoke(handler, event);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      this.logManager.warn("No handler found for event: %s".formatted(eventType));
    }
    return;
  }
}
