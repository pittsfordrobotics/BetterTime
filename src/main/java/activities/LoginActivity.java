package activities;

import databases.BatchUpdateData;
import databases.DatabaseUtils;
import helpers.Constants;
import java.util.ArrayList;

public class LoginActivity {
  private DatabaseUtils dbUtils;

  public LoginActivity(DatabaseUtils dbUtils) {
    this.dbUtils = dbUtils;
  }

  public void loginUser(int userRow, String currentTime) {
    ArrayList<BatchUpdateData> data = new ArrayList<>();

    data.add(new BatchUpdateData(userRow, Constants.kLastLoginColumn, currentTime));
    data.add(new BatchUpdateData(userRow, Constants.kLoggedInColumn, "TRUE"));
    data.add(new BatchUpdateData(userRow, Constants.kLastLogoutColumn, "LOGGED IN"));

    dbUtils.setCellDataBatch(data, Constants.kMainSheet);
  }
}
