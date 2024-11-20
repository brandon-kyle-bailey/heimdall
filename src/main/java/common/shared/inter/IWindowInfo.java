package common.shared.inter;

import java.time.LocalDateTime;

public interface IWindowInfo {
  String getApplicationName();

  String getWindowTitle();

  String getUrl();

  LocalDateTime getTimestamp();
}
