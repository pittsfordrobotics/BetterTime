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

    // String firstName =
    //     dbUtils.getCellData(studentIDRow, Constants.kFirstNameColumn, Constants.kMainSheet);
    // String lastName =
    //     dbUtils.getCellData(studentIDRow, Constants.kLastNameColumn, Constants.kMainSheet);

    // ArrayList<String> firstNamesListReg = dbUtils.getColumnData(0, Constants.kRegistrationSheet);
    // ArrayList<String> lastNamesListReg = dbUtils.getColumnData(1, Constants.kRegistrationSheet);

    // // check if first and last names are found
    // if (matchName(firstName, firstNamesListReg)) {
    //   LoggingUtils.log(Level.INFO, firstName + " was detected in user registration");
    //   if (matchName(lastName, lastNamesListReg)) {
    //     LoggingUtils.log(Level.INFO, lastName + " was detected in user registration");
    //     return true;
    //   }

    //   LoggingUtils.log(Level.INFO, lastName + " was not detected in user registration");

    //   return false;
    // }

    // LoggingUtils.log(Level.INFO, firstName + " was not detected in user registration");
  }

  // private boolean matchName(String name, ArrayList<String> nameList) {
  //   name = name.toLowerCase();

  //   for (String nameInList : nameList) {
  //     if (nameInList.equalsIgnoreCase(name)) {
  //       return true;
  //     }
  //   }

  //   return false;
  // }
}
