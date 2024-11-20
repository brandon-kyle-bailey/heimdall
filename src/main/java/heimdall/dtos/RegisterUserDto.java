package heimdall.dtos;

public class RegisterUserDto {
  private String userId;
  private String accountId;

  public RegisterUserDto(String userId, String accountId) {
    this.userId = userId;
    this.accountId = accountId;
  }

  // Getters
  public String getUserId() {
    return userId;
  }

  public String getAccountId() {
    return accountId;
  }
}
