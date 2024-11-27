package heimdall.entities;

import java.time.LocalDateTime;

public class ActivityEntity {
  private int id;
  private String name;
  private String title;
  private String url;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private Integer duration;

  public ActivityEntity(int id, String name, String title, String url, LocalDateTime startTime,
      LocalDateTime endTime, Integer duration) {
    this.id = id;
    this.name = name;
    this.title = title;
    this.url = url;
    this.startTime = startTime;
    this.endTime = endTime;
    this.duration = duration;
  }

  // Getters and setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
}
