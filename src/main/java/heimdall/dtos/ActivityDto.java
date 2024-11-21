package heimdall.dtos;

import java.time.LocalDateTime;

public class ActivityDto {
  private int id;
  private String appName;
  private String title;
  private String url;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private Integer duration;

  public ActivityDto(int id, String appName, String title, String url, LocalDateTime startTime,
      LocalDateTime endTime, Integer duration) {
    this.id = id;
    this.appName = appName;
    this.title = title;
    this.url = url;
    this.startTime = startTime;
    this.endTime = endTime;
    this.duration = duration;
    // if (this.endTime != null) {
    // this.duration = (int) ChronoUnit.MILLIS.between(this.startTime,
    // this.endTime);
    // }
  }

  // Getters and setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
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

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  // public Integer getDuration() {
  // if (this.endTime != null) {
  // this.duration = (int) ChronoUnit.MILLIS.between(this.startTime,
  // this.endTime);
  // }
  // return duration;
  // }
}
