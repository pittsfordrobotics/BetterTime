package notifiers;

import databases.DatabaseUtils;
import helpers.Constants;
import helpers.LoggingUtils;
import java.util.logging.Level;

/**
 * @author Dalton Smith LoginNotifier Checks if user account registration is complete, this code is
 *     Grizzly Robotics specific
 */
public class LoginNotifier {
  public NotifierResult checkNotifier(int studentIDRow, DatabaseUtils dbUtils) {
    String msg = dbUtils.getCellData(studentIDRow, Constants.kImportantMessageColumn, Constants.kMainSheet);
    
    if (!msg.equalsIgnoreCase("")) {
      String studentId = dbUtils.getCellData(studentIDRow, Constants.kStudentIdColumn, Constants.kMainSheet);
      LoggingUtils.log(Level.INFO, studentId + " has a message: " + msg);
    }

    return new NotifierResult(msg);
  }
}
