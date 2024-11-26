// package heimdall.handlers;
//
// import java.sql.SQLException;
// import java.util.List;
//
// import org.json.JSONObject;
//
// import heimdall.common.interfaces.IEventHandler;
// import heimdall.ports.LoggerPort;
// import heimdall.repositories.UserRepository;
// import heimdall.dtos.UserEntity;
// import heimdall.dtos.mappers.UserDtoMapper;
//
// public class CreateUserEventHandler implements IEventHandler {
// private UserRepository repository;
//
// public CreateUserEventHandler(UserRepository repository) {
// this.repository = repository;
// }
//
// @Override
// public Object handle(JSONObject event) {
// LoggerPort.debug("CreateUserEventHandler.handle invoked:
// %s".formatted(event));
// try {
// event.put("id", 0);
// UserEntity user = UserDtoMapper.interfaceToDomain(event);
// List<UserEntity> foundUsers =
// repository.findUsersByAccountIdAndUserId(user.getAccountId(),
// user.getUserId());
// UserEntity foundUser = foundUsers.isEmpty() ? null : foundUsers.get(0);
// if (foundUser != null) {
// LoggerPort.debug("User %s already exists.".formatted(user.getUserId()));
// return null;
// }
// repository.create(user);
// } catch (SQLException e) {
// e.printStackTrace();
// }
// return event;
// }
//
// }
