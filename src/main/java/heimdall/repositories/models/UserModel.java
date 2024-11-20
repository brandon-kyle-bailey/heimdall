package heimdall.repositories.models;

public class UserModel {
  private int id;
  private String userId;
  private String accountId;

  public UserModel(int id, String userId, String accountId) {
    this.id = id;
    this.userId = userId;
    this.accountId = accountId;
  }

  // Getters and setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

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
