package heimdall.dtos;

public class CreateApplicationDto {
  private String name;

  public CreateApplicationDto(String name) {
    this.name = name;
  }

  // Getters
  public String getName() {
    return name;
  }
}
