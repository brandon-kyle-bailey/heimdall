package heimdall.adapters;

import java.util.HashMap;
import java.util.Map;

import heimdall.ports.LoggerPort;

public class EventbusAdapter {

  private Map<String, Object> eventHandlers = new HashMap<>();

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
      LoggerPort.warn("No handler found for event: %s".formatted(eventType));
    }
  }
}
