// package heimdall.handlers;
//
// import java.sql.SQLException;
// import java.time.temporal.ChronoUnit;
// import java.util.List;
//
// import org.json.JSONObject;
//
// import heimdall.common.interfaces.IEventHandler;
// import heimdall.dtos.ActivityEntity;
// import heimdall.dtos.mappers.ActivityDtoMapper;
// import heimdall.ports.LoggerPort;
// import heimdall.repositories.ActivityRepository;
//
// public class UpsertActivityEventHandler implements IEventHandler {
// private ActivityRepository repository;
//
// public UpsertActivityEventHandler(ActivityRepository repository) {
// this.repository = repository;
// }
//
// @Override
// public Object handle(JSONObject event) {
// LoggerPort.debug("UpsertActivityEventHandler.handle invoked:
// %s".formatted(event));
//
// try {
// event.put("id", 0);
// ActivityEntity activity = ActivityDtoMapper.interfaceToDomain(event);
//
// List<ActivityEntity> foundActivities =
// repository.findLatestByNameTitleUrl(activity.getName(),
// activity.getTitle(),
// activity.getUrl());
// ActivityEntity foundActivity = foundActivities.isEmpty() ? null :
// foundActivities.get(0);
//
// if (foundActivity != null) {
// // update the found activities end time for duration calculation
// foundActivity.setEndTime(activity.getEndTime());
//
// if (foundActivity.getStartTime() != null && foundActivity.getEndTime() !=
// null) {
// int duration = (int) ChronoUnit.MILLIS.between(foundActivity.getStartTime(),
// foundActivity.getEndTime());
// foundActivity.setDuration(duration);
// }
//
// repository.update(foundActivity);
// return null;
// }
//
// activity.setDuration(null);
// repository.create(activity);
//
// } catch (SQLException e) {
// LoggerPort.error("SQL Error during UpsertActivityEventHandler.handle:
// %s".formatted(e.getMessage()));
// e.printStackTrace();
// } catch (Exception e) {
// LoggerPort.error("Unexpected error during UpsertActivityEventHandler.handle:
// %s".formatted(e.getMessage()));
// e.printStackTrace();
// }
//
// return event;
// }
// }
