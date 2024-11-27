package heimdall.interfaces.dtos.app;

public class CreateAppDto {
  private String name;

  public CreateAppDto(String name) {
    this.name = name;
  }

  // Getters and setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
