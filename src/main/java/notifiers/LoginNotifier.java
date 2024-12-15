package notifiers;

import databases.DatabaseUtils;
import helpers.AlertUtils;
import helpers.CommonUtils;
import helpers.Constants;
import helpers.LoggingUtils;
import java.util.logging.Level;

/**
 * @author Dalton Smith LoginNotifier Checks if user account registration is
 *         complete, this code is
 *         Grizzly Robotics specific
 */
public class LoginNotifier {
  private DatabaseUtils dbUtils;

  private AlertUtils alertUtils = new AlertUtils();
  private CommonUtils utils = new CommonUtils();

  public LoginNotifier(DatabaseUtils dbUtils) {
    this.dbUtils = dbUtils;
  }

  public void showLoginMessages(String userId) {
    String msg = getMessageForUserId(userId, dbUtils);

    if (msg == null || msg == "") {
      return;
    }

    utils.playDing();
    alertUtils.createAlert("Important message!", "Important message!", msg);
  }

  public void showLogoutMessages(String userId) {
    String msg = getMessageForUserId(userId, dbUtils);

    if (msg == null || msg == "") {
      return;
    }

    utils.playDing();
    alertUtils.createAlert("Reminder!", "Did you remember?", msg);
  }

  private String getMessageForUserId(String userId, DatabaseUtils dbUtils) {
    int userRow = dbUtils.getCellRowFromColumn(userId, Constants.kStudentIdColumn, Constants.kMainSheet);
    if (userRow == -1) {
      // Not found
      return null;
    }

    return dbUtils.getCellData(userRow, Constants.kImportantMessageColumn, Constants.kMainSheet);
  }
}