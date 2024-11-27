package heimdall.interfaces.dtos.activity;

import java.time.LocalDateTime;

public class UpsertActivityDto {
  private String app;
  private String title;
  private String url;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private int duration;

  public UpsertActivityDto(String app, String title, String url, LocalDateTime startTime, LocalDateTime endTime,
      int duration) {
    this.app = app;
    this.title = title;
    this.url = url;
    this.startTime = startTime;
    this.endTime = endTime;
    this.duration = duration;
  }

  public String getApp() {
    return app;
  }

  public void setApp(String app) {
    this.app = app;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }
}
