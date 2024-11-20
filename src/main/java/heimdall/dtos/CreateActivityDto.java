package heimdall.dtos;

import java.time.LocalDateTime;

public class CreateActivityDto {
  private String appName;
  private String title;
  private String url;
  private LocalDateTime startTime;
  private LocalDateTime endTime;

  public CreateActivityDto(String appName, String title, String url, LocalDateTime startTime,
      LocalDateTime localDateTime) {
    this.appName = appName;
    this.title = title;
    this.url = url;
    this.startTime = startTime;
    this.endTime = localDateTime;
  }

  // Getters
  public String getAppName() {
    return appName;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }
}
