package org.example;
import java.sql.*;
import java.util.Scanner;


public class Profile {

    public static int createProfile() {
        Scanner scanner = Main.scanner;
        System.out.println("Enter your first name:");
        String firstName = scanner.nextLine();

        System.out.println("Enter your last name:");
        String lastName = scanner.nextLine();

        System.out.println("Enter your age:");
        int age = scanner.nextInt();

        System.out.println("Enter your weight:");
        float weight = scanner.nextFloat();

        System.out.println("Enter your height:");
        float height = scanner.nextFloat();

        System.out.println("Enter your body fat percentage:");
        float bodyFatPercentage = scanner.nextFloat();
        scanner.nextLine();

        System.out.println("Enter your health conditions (comma-separated list):");
        String[] healthConditions = scanner.nextLine().split(",");

        System.out.println("Enter your goal weight:");
        int goalWeight = scanner.nextInt();

        System.out.println("Enter your goal date (YYYY-MM-DD):");
        String goalDateStr = scanner.next();
        java.sql.Date goalDate = java.sql.Date.valueOf(goalDateStr);

        String profileSql = "INSERT INTO Profile (first_name, last_name, goal_weight, goal_date, health_id, schedules) VALUES (?, ?, ?, ?,?,?)";

        int healthDetails = Health.makeHealth(age, weight, height, bodyFatPercentage, healthConditions);
        if (healthDetails == -1) {
            System.out.println("Health details have not been added. Please check the information you have entered.");
            return -1; // Return -1 if health details cannot be created
        }

        String currentDate = getCurrentDate();
        int scheduleId = Schedule.createSchedule(currentDate);
        try {
            Connection connection = Main.dbConnection;
            PreparedStatement profileStmt = connection.prepareStatement(profileSql, Statement.RETURN_GENERATED_KEYS);

            profileStmt.setString(1, firstName);
            profileStmt.setString(2, lastName);
            profileStmt.setInt(3, goalWeight);
            profileStmt.setDate(4, goalDate);
            profileStmt.setInt(5, healthDetails);
            Array schedulesArray = connection.createArrayOf("INTEGER", new Integer[]{scheduleId}); // Wrap scheduleId in an array
            profileStmt.setArray(6, schedulesArray);
            profileStmt.executeUpdate();

            ResultSet generatedKeys = profileStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int profileId = generatedKeys.getInt(1);
                System.out.println("Profile created successfully with ID: " + profileId);
                return profileId;
            } else {
                System.out.println("Failed to retrieve generated keys for the newly created profile.");
                return -1; // Return -1 if generated keys cannot be retrieved
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Return -1 if an SQL exception occurs
        }
    }


    public static void updateProfile(int profileId, String fname, String lname, int age, float height, float weight, float bodyFatPercentage, String[] healthNotes, int weightGoal, Date goalDeadline) {
        String profileSql = "UPDATE Profile SET first_name = ?, last_name = ?, goal_weight = ?, goal_date = ? WHERE id = ?";
        int existingHealthId = getHealthIdForProfile(profileId); // Retrieve existing health ID

        try{
            Connection connection = Main.dbConnection;
            PreparedStatement profileStmt = connection.prepareStatement(profileSql);

            // Update profile data
            profileStmt.setString(1, fname);
            profileStmt.setString(2, lname);
            profileStmt.setInt(3, weightGoal);
            profileStmt.setDate(4, goalDeadline);
            profileStmt.setInt(5, profileId);
            profileStmt.executeUpdate();

            if (existingHealthId != -1) {
                Health.updateHealth(existingHealthId, age, weight, height, bodyFatPercentage, healthNotes);
            } else {
                System.out.println("No existing health details found for the profile.");
            }

            System.out.println("Profile updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getHealthIdForProfile(int profileId) {
        String healthIdQuery = "SELECT health_id FROM Profile WHERE id = ?";
        try {
            Connection connection = Main.dbConnection;
            PreparedStatement stmt = connection.prepareStatement(healthIdQuery);
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("health_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    private static String getCurrentDate() {
        java.util.Date date = new java.util.Date();
        return new java.sql.Date(date.getTime()).toString();
    }

    public static boolean isProfileExists(int profileID) {
        Connection conn = Main.dbConnection;
        try {
            String sql = "SELECT id FROM Profile WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, profileID);
                ResultSet rs = pstmt.executeQuery();
                return rs.next(); // If next() returns true, profile exists
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to check profile existence.");
            e.printStackTrace();
            return false;
        }
    }

    public static int getProfileScheduleId(int profileId)
    {
        Connection conn = Main.dbConnection;

        String sql = "SELECT schedules FROM Profile WHERE id = ?";
        try ( PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, profileId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Array schedulesArray = rs.getArray("schedules");
                Integer[] schedules = (Integer[]) schedulesArray.getArray();
                return schedules[0]; //for now profile only has one sched
            } else {
                System.out.println("Profile not found.");
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static void addGroupClassToSchedule(int profID)
    {
        Scanner scanner = Main.scanner;

        System.out.println("Enter the class ID you'd like to add to your schedule");
        int classID = scanner.nextInt();

        int schedID = getProfileScheduleId(profID);

        Schedule.addClassToSchedule(schedID,classID);

    }


}

