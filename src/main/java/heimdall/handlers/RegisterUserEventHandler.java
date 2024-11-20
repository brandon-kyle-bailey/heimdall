package heimdall.handlers;

import java.sql.SQLException;

import heimdall.dtos.RegisterUserDto;
import heimdall.repositories.UserPersistenceRepository;
import heimdall.repositories.models.UserModel;

public class RegisterUserEventHandler {
  UserPersistenceRepository repository;

  public RegisterUserEventHandler(UserPersistenceRepository repository) {
    this.repository = repository;
  }

  public UserModel handle(RegisterUserDto event) {
    System.out.println("RegisterUserEventHandler.handle invoked: %s".formatted(event.toString()));
    UserModel user = new UserModel(0, event.getUserId(), event.getAccountId());
    try {
      UserModel foundUser = this.repository.findUsersByAccountIdAndUserId(event.getAccountId(), event.getUserId())
          .get(0);
      if (foundUser != null) {
        System.out.println("User already exists.");
        return null;
      }
      this.repository.create(user);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return user;
  }
}
