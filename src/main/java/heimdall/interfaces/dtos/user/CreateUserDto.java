package heimdall.interfaces.dtos.user;

public class CreateUserDto {
  private String userId;
  private String accountId;

  public CreateUserDto(String userId, String accountId) {
    this.userId = userId;
    this.accountId = accountId;
  }

  // Getters and setters
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }
}
