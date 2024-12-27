package activities;

import java.util.ArrayList;
import java.util.function.Consumer;

import helpers.AlertUtils;

public class LogoutAllActivity {
    private AlertUtils alertUtils = new AlertUtils();
    private UserActivity userActivity = new UserActivity();

    public void logOutAllUsers(String initiatorId, Consumer<String> displayMessage) {
        if (!userActivity.isMentorId(initiatorId)) {
            alertUtils.createAlert("Denied", "Mentors only", "The user id is not a mentor.");
            return;
        }

        boolean proceed = alertUtils.createAlert("Confirm", "Log out everyone?", "Please confirm you wish to log out all users.");
        if (!proceed) {
            return;
        }

        displayMessage.accept("Finding logged in users...");
        ArrayList<String> loggedInUsers = userActivity.getLoggedInUsers();
        for (int i = 0; i < loggedInUsers.size(); i++) {
            displayMessage.accept("Logging out user " + (i + 1) + " of " + loggedInUsers.size());
            String userId = loggedInUsers.get(i);
            userActivity.logoutUser(userId);
        }

        displayMessage.accept("Logged out " + loggedInUsers.size() + " users.");
    }
}
