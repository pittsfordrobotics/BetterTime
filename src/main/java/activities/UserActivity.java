package activities;

import databases.BatchUpdateData;
import databases.DatabaseUtils;
import exceptions.CancelledUserCreationException;
import helpers.AlertUtils;
import helpers.Constants;
import helpers.LoggingUtils;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.application.Platform;
import notifiers.LoginNotifier;
import scenes.GrizzlyScene;

public class UserActivity {
  /**
   * @author Dalton Smith UserActivity class Contains the various methods for handling user
   *     login/logout
   */
  private DatabaseUtils dbUtils = new DatabaseUtils();
  private AlertUtils alertUtils = new AlertUtils();

  private LogoutActivity logoutActivity = new LogoutActivity(dbUtils);
  private LoginActivity loginActivity = new LoginActivity(dbUtils);
  private LoginNotifier notifier = new LoginNotifier(dbUtils);

  private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

  public ArrayList<String> getLoggedInUsers() {
    ArrayList<String> loggedInIds = new ArrayList<>();

    ArrayList<String> ids = getAllUserIds();
    for (String id : ids) {
      if (doesIdExist(ids, id) == Constants.kIdLoggedIn) {
        loggedInIds.add(id);
      }
    }

    return loggedInIds;
  }

  // check if user is logged in
  public boolean isUserLoggedIn(String userID) throws Exception {
    dbUtils.getUpdatedData();

    ArrayList<String> ids = getAllUserIds();

    int state = doesIdExist(ids, userID);

    switch (state) {
      case Constants.kIdDoesNotExist:
        break;
      case Constants.kIdLoggedIn:
        return true;
      case Constants.kIdNotLoggedIn:
        return false;
      default:
        LoggingUtils.log(Level.SEVERE, "Uh oh, isUserLoggedIn received an unknown ID of " + state);
        break;
    }

    // request users first name and last name
    LoggingUtils.log(Level.INFO, "New User Detected");
    ArrayList<String> userData = alertUtils.getUserInfo();

    // throws CancelledUserException if registration was cancelled
    createNewUser(userData, userID);

    return false;
  }

  public void createNewUser(ArrayList<String> userData, String userID)
      throws CancelledUserCreationException {
    // cancel if user cancelled or exited registration dialog
    if (("TRUE").equalsIgnoreCase(userData.get(0))) {
      // create user then login
      ArrayList<BatchUpdateData> data = new ArrayList<>();

      int blankRow = dbUtils.nextEmptyCellColumn(Constants.kMainSheet);
      addUserInfoBasic(userData, userID, data, blankRow);
      data.add(new BatchUpdateData(blankRow, Constants.kRoleColumn, userData.get(3)));

      dbUtils.setCellDataBatch(data, Constants.kMainSheet);
      dbUtils.getUpdatedData();

      ArrayList<String> columnLogged =
          dbUtils.getColumnData(Constants.kStudentIdColumn, Constants.kLogSheet);

      int i;
      for (i = 1; i < columnLogged.size(); i++) {
        if (columnLogged.get(i).equals("")) {
          break;
        }
      }

      data.clear();
      addUserInfoBasic(userData, userID, data, i);

      dbUtils.setCellDataBatch(data, Constants.kLogSheet);
      dbUtils.getUpdatedData();

      // ensure there is a date column
      logoutActivity.getCurrentDateColumn();

    } else {
      LoggingUtils.log(Level.INFO, "Account Creation Cancelled");
      throw new CancelledUserCreationException("Cancelled");
    }
  }

  private void addUserInfoBasic(
    ArrayList<String> userData, String userID, ArrayList<BatchUpdateData> data, int i) {
    data.add(new BatchUpdateData(i, Constants.kStudentIdColumn, userID));
    data.add(new BatchUpdateData(i, Constants.kFirstNameColumn, userData.get(1)));
    data.add(new BatchUpdateData(i, Constants.kLastNameColumn, userData.get(2)));
  }

  public boolean isMentorId(String userId) {
    dbUtils.getUpdatedData();
    ArrayList<String> ids = getAllUserIds();
    for (int i = 0; i < ids.size(); i++) {
      if (ids.get(i).equals(userId)) {
        String mentorData = dbUtils.getCellData(i, Constants.kRoleColumn, Constants.kMainSheet);
        return "MENTOR".equalsIgnoreCase(mentorData);
      }
    }

    // not found
    return false;
  }

  public int doesIdExist(ArrayList<String> ids, String userID) {
    // check if the user ID exists
    for (int i = 0; i < ids.size(); i++) {
      // if the user exists, check if logged in or logged out and return state
      if (ids.get(i).equals(userID)) {
        String cellData = dbUtils.getCellData(i, Constants.kLoggedInColumn, Constants.kMainSheet);
        try {
          cellData = cellData.replaceAll("\\s+", "");
        } catch (NullPointerException e) {
          continue;
          // do nothing because the cell doesn't exist?
        }

        if (cellData.equals("TRUE")) {
          return Constants.kIdLoggedIn;
        } else {
          return Constants.kIdNotLoggedIn;
        }
      }
    }

    return Constants.kIdDoesNotExist;
  }

  // login our user
  public void loginUser(String userID) {
    Platform.runLater(() -> GrizzlyScene.setMessageBoxText("Logging in user!"));

    // grab the current time from system and format it into string
    LocalDateTime loginTime = LocalDateTime.now();
    String formattedLoginTime = loginTime.format(formatter);

    int userRow = dbUtils.getCellRowFromColumn(userID, Constants.kStudentIdColumn, Constants.kMainSheet);

    // log the user in
    if (userRow != -1) {
      loginActivity.loginUser(userRow, formattedLoginTime);
      Platform.runLater(
          () -> {
            GrizzlyScene.setMessageBoxText("Successfully logged in user!");
            GrizzlyScene.clearInput();
          });
    }
  }

  // logout the user
  public void logoutUser(String userID) {
    Platform.runLater(() -> GrizzlyScene.setMessageBoxText("Logging out user!"));

    // grab the row the user is on
    int userRow =
        dbUtils.getCellRowFromColumn(userID, Constants.kStudentIdColumn, Constants.kMainSheet);

    // grab last logged in time
    LocalDateTime logoutTime = LocalDateTime.now();
    LocalDateTime loginTime =
        LocalDateTime.parse(
            dbUtils.getCellData(userRow, Constants.kLastLoginColumn, Constants.kMainSheet),
            formatter);

    String formattedLogoutTime = logoutTime.format(formatter);

    // assuming userRow isn't invalid, calculate difference in time and log hours
    if (userRow != -1) {
      // update the logout time
      dbUtils.setCellData(
          userRow, Constants.kLastLogoutColumn, formattedLogoutTime, Constants.kMainSheet);

      int diffHours = logoutTime.getHour() - loginTime.getHour();
      int diffMinutes = logoutTime.getMinute() - loginTime.getMinute();
      int diffSeconds = logoutTime.getSecond() - loginTime.getSecond();

      boolean err = false;

      if (diffHours < 0) {
        LoggingUtils.log(
            Level.SEVERE,
            "Well this is awkward, difference shouldn't be negative: h:"
                + diffHours
                + " m:"
                + diffMinutes
                + " s:"
                + diffSeconds);
        err = true;
      }

      if (diffSeconds < 0) {
        diffMinutes -= 1;
        diffSeconds = 60 - Math.abs(diffSeconds);
      }

      if (diffMinutes < 0) {
        diffHours -= 1;
        diffMinutes = 60 - Math.abs(diffMinutes);
      }

      if (loginTime.getYear() == logoutTime.getYear()) {
        if (loginTime.getMonth() == logoutTime.getMonth()) {
          if (loginTime.getDayOfMonth() != logoutTime.getDayOfMonth()) {
            err = true;
          }
        } else {
          err = true;
        }
      } else {
        err = true;
      }

      if (!err) {
        String totalTimeFromDifference =
            String.format("%02d:%02d:%02d", diffHours, diffMinutes, diffSeconds);
        LocalTime totalHoursTime = LocalTime.parse(totalTimeFromDifference);

        logoutActivity.logoutUser(userID, userRow, totalHoursTime, totalTimeFromDifference);

        // show user logout text
        Platform.runLater(
          () -> {
            GrizzlyScene.setMessageBoxText("Logged out user!");
            GrizzlyScene.clearInput();
          });
      }

      // logout the user
      dbUtils.setCellData(userRow, Constants.kLoggedInColumn, "FALSE", Constants.kMainSheet);

      if (err) {
        Platform.runLater(
            () -> {
              GrizzlyScene.setMessageBoxText("You forgot to log out! Please re-login!");
              GrizzlyScene.clearInput();
            });
      }
    }
  }

  // checks if ID is valid long and x digit number (x based on config file)
  public boolean isValidID(String userID) {
    int idLength = LocalDbActivity.kIdLength;
    int mentorIdLength = LocalDbActivity.kIdLengthFallback;

    try {
      Long.parseLong(userID);

      if (Constants.kMentorFallback) {
        return userID.length() == idLength || userID.length() == mentorIdLength;

      } else {
        return userID.length() == idLength;
      }

    } catch (NumberFormatException e) {
      // not a valid ID
      return false;
    }
  }

  public void showLoginMessages(String userId) {
    notifier.showLoginMessages(userId);
  }

  public void showLogoutMessages(String userId) {
    notifier.showLogoutMessages(userId);
  }

  private ArrayList<String> getAllUserIds() {
    return dbUtils.getColumnData(0, Constants.kMainSheet);
  }
}