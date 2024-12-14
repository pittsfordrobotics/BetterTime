package activities;

import java.util.ArrayList;
import helpers.AlertUtils;

public class LogoutAllActivity {
    private AlertUtils alertUtils = new AlertUtils();
    private UserActivity userActivity = new UserActivity();

    public void logOutAllUsers(String initiatorId) {
        if (!userActivity.isMentorId(initiatorId)) {
            alertUtils.createAlert("Denied", "Mentors only", "The user id is not a mentor.");
            return;
        }

        boolean proceed = alertUtils.createAlert("Confirm", "Log out everyone?", "Please confirm you wish to log out all users.");
        if (!proceed) {
            alertUtils.createAlert("Cancelled", "Action cancelled", "Logout has been cancelled.");
            return;
        }

        ArrayList<String> loggedInUsers = userActivity.getLoggedInUsers();
        for (String userId : loggedInUsers) {
            userActivity.logoutUser(userId, false);
        }

        alertUtils.createAlert("Done", "Log out completed", "Logged out " + loggedInUsers.size() + " users.");
    }
}
