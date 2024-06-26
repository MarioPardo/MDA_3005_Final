package org.example;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Trainer
{

    public static int TrainerSignIn(String username, String password) {
        Connection conn = Main.dbConnection;

        String sql = "SELECT * FROM Trainer WHERE username = ? AND password = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int trainerId = rs.getInt("id");
                System.out.println("Login successful!");
                return trainerId;
            } else {
                System.out.println("Incorrect username or password.");
                return -1;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static void createTrainerRoutine(int trainerID)
    {
        int routineID = Routine.createRoutine();

        if(routineID == -1)
            return;

        addRoutineToTrainer(trainerID, routineID);
    }

    public static void addRoutineToTrainer(int trainerID, int routineID) {
        Connection conn = Main.dbConnection;

        int[] currentRoutines = getTrainerRoutines(trainerID);

        int[] updatedRoutines = Arrays.copyOf(currentRoutines, currentRoutines.length + 1);
        updatedRoutines[currentRoutines.length] = routineID;

        updateTrainerRoutines(trainerID, updatedRoutines);

        System.out.println("Routine with ID " + routineID + " added to the trainer with ID " + trainerID + " successfully.");
    }


    private static int[] getTrainerRoutines(int trainerID) {
        Connection conn = Main.dbConnection;
        try {
            String sql = "SELECT routines FROM Trainer WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, trainerID);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    Array routinesArray = rs.getArray("routines");
                    if(routinesArray == null)
                        routinesArray = conn.createArrayOf("INTEGER", new Integer[0]);
                    Integer[] routines = (Integer[]) routinesArray.getArray();
                    return Arrays.stream(routines).mapToInt(Integer::intValue).toArray();
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to retrieve current routines.");
            e.printStackTrace();
        }
        return new int[0];
    }

    private static void updateTrainerRoutines(int trainerID, int[] updatedRoutines)
    {
        Connection conn = Main.dbConnection;
        try {
            String sql = "UPDATE Trainer SET routines = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setArray(1, conn.createArrayOf("INTEGER", Arrays.stream(updatedRoutines).boxed().toArray()));
                pstmt.setInt(2, trainerID);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to update trainer routines.");
            e.printStackTrace();
        }
    }

    public static void addRoutineToProfile()
    {
        Connection conn = Main.dbConnection;
        Scanner scanner = Main.scanner;

        System.out.print("Enter profile ID: ");
        int profileID = scanner.nextInt();

        if(!Profile.isProfileExists(profileID))
        {
            System.out.println("No profile exists with ID:" + profileID);
            return;
        }

        scanner.nextLine();

        System.out.print("Enter routine ID: ");
        int routineID = scanner.nextInt();

        if(!Routine.isRoutineExists(routineID))
        {
            System.out.println("No Routine exists with ID:" + routineID);
            return;
        }

        int[] currentRoutines = getProfileRoutines(profileID);

        int[] updatedRoutines = Arrays.copyOf(currentRoutines, currentRoutines.length + 1);
        updatedRoutines[currentRoutines.length] = routineID;

        updateProfileRoutines(profileID, updatedRoutines);

        System.out.println("Routine with ID " + routineID + " added to the profile with ID " + profileID + " successfully.");
    }

    private static int[] getProfileRoutines(int profileID) {
        Connection conn = Main.dbConnection;
        try {
            String sql = "SELECT routines FROM Profile WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, profileID);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    Array routinesArray = rs.getArray("routines");
                    if (routinesArray == null)
                        routinesArray = conn.createArrayOf("INTEGER", new Integer[0]);
                    Integer[] routines = (Integer[]) routinesArray.getArray();
                    return Arrays.stream(routines).mapToInt(Integer::intValue).toArray();
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to retrieve current routines.");
            e.printStackTrace();
        }
        return new int[0];
    }

    private static void updateProfileRoutines(int profileID, int[] updatedRoutines) {
        Connection conn = Main.dbConnection;
        try {
            String sql = "UPDATE Profile SET routines = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setArray(1, conn.createArrayOf("INTEGER", Arrays.stream(updatedRoutines).boxed().toArray()));
                pstmt.setInt(2, profileID);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to update profile routines.");
            e.printStackTrace();
        }
    }
    public static void showTrainerClients(int trainerId) {
        Connection conn = Main.dbConnection;
        try {
            String sql = "SELECT p.id, p.first_name, p.last_name " +
                    "FROM Profile p " +
                    "JOIN Trainer t ON p.id = ANY(t.clients) " +
                    "WHERE t.id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, trainerId);
                ResultSet rs = pstmt.executeQuery();
                boolean foundClients = false;
                System.out.println("Clients of Trainer with ID " + trainerId + ":");
                while (rs.next()) {
                    foundClients = true;
                    int clientId = rs.getInt("id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    System.out.println("Client ID: " + clientId + ", Name: " + firstName + " " + lastName);
                }
                if (!foundClients) {
                    System.out.println("No clients found for Trainer with ID " + trainerId);
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: An error occurred while fetching clients.");
            e.printStackTrace();
        }
    }
    public static void showTrainerRoutines(int trainerId) {
        Connection conn = Main.dbConnection;
        try {
            String sql = "SELECT r.id AS routine_id, r.name AS routine_name, r.exercises AS exercises " +
                    "FROM Routine r " +
                    "JOIN Trainer t ON r.id = ANY(t.routines) " +
                    "WHERE t.id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, trainerId);
                ResultSet rs = pstmt.executeQuery();
                System.out.println("Routines and Exercises of Trainer with ID " + trainerId + ":");
                boolean foundRoutines = false;
                while (rs.next()) {
                    foundRoutines = true;
                    int routineId = rs.getInt("routine_id");
                    String routineName = rs.getString("routine_name");
                    Array exercisesArray = rs.getArray("exercises");
                    String[] exercises = (String[]) exercisesArray.getArray();
                    System.out.println("Routine ID: " + routineId + ", Name: " + routineName);
                    System.out.println("    Exercises:");
                    for (String exercise : exercises) {
                        System.out.println("    - " + exercise);
                    }
                }
                if (!foundRoutines) {
                    System.out.println("Trainer with ID " + trainerId + " has no routines.");
                }
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while fetching routines.");
            e.printStackTrace();
        }
    }

    public static String getTrainerName(int trainerId) {
        String trainerName = "NONE";

        Connection conn = Main.dbConnection;

        String sql = "SELECT first_name, last_name FROM Trainer WHERE id = ?";
        try (
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, trainerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                trainerName = firstName + " " + lastName;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trainerName;
    }

    public static void printTrainerSchedule(int trainerId) {
        Connection conn = Main.dbConnection;
        try {
            String trainerSql = "SELECT first_name, last_name, working_hours FROM Trainer WHERE id = ?";
            PreparedStatement trainerStmt = conn.prepareStatement(trainerSql);
            trainerStmt.setInt(1, trainerId);
            ResultSet trainerRs = trainerStmt.executeQuery();

            // Print trainer's name and working hours
            if (trainerRs.next()) {
                String firstName = trainerRs.getString("first_name");
                String lastName = trainerRs.getString("last_name");
                Array workingHoursArray = trainerRs.getArray("working_hours");
                String[] workingHours = (String[]) workingHoursArray.getArray();

                System.out.println("Trainer: " + firstName + " " + lastName + "  with ID:" + trainerId);
                System.out.print("Working Hours: " + workingHours[0] + " - " + workingHours[1] + "\n");
            }

            // Retrieve classes for the trainer
            String classSql = "SELECT id, time, is_group, room_number, participants FROM Class WHERE trainer_id = ?";
            PreparedStatement classStmt = conn.prepareStatement(classSql);
            classStmt.setInt(1, trainerId);
            ResultSet classRs = classStmt.executeQuery();

            // Print classes
            while (classRs.next()) {
                int classId = classRs.getInt("id");
                Time classTime = classRs.getTime("time");
                Time endTime = Time.valueOf(classTime.toLocalTime().plusHours(1));
                boolean isGroup = classRs.getBoolean("is_group");
                Array participantsArray = classRs.getArray("participants");

                Integer[] participants = null;
                if(participantsArray != null)
                    participants = (Integer[]) participantsArray.getArray();

                if (isGroup) {
                    System.out.println("    * Teaching group class at " + classTime.toString().substring(0, 5) + " - " + endTime.toString().substring(0, 5));
                } else {
                    System.out.print("    * Personal training class " + classTime.toString().substring(0, 5) + " - " + endTime.toString().substring(0, 5));
                    // Print participants' names
                    if(participants != null)
                        for (int participantId : participants) {
                            String participantName = Profile.getProfileName(participantId);
                            System.out.print(" with client " + participantName + "\n");
                        }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkTrainerExists(int trainerId) {
        Connection conn = Main.dbConnection;
        boolean trainerExists = false;
        try {
            String sql = "SELECT COUNT(*) AS count FROM Trainer WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                trainerExists = count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception here (printing or logging)
        }
        return trainerExists;
    }



    public static LocalTime[] getTrainerWorkingHours(int trainerId)
    {
        Connection connection = Main.dbConnection;
        LocalTime[] workingHours = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT working_hours FROM Trainer WHERE id = ?")) {
            statement.setInt(1, trainerId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Array workingHoursArray = resultSet.getArray("working_hours");
                String[] workingHoursStrings = (String[]) workingHoursArray.getArray();
                workingHours = new LocalTime[2];
                workingHours[0] = LocalTime.parse(workingHoursStrings[0]);
                workingHours[1] = LocalTime.parse(workingHoursStrings[1]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workingHours;
    }

    public static List<LocalTime[]> getTrainerBookedHours(int trainerId)
    {
        List<LocalTime[]> bookedHours = new ArrayList<>();
        Connection connection = Main.dbConnection;
        try (
             PreparedStatement statement = connection.prepareStatement("SELECT time FROM Class WHERE trainer_id = ?")) {
            statement.setInt(1, trainerId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                LocalTime startTime = resultSet.getTime("time").toLocalTime();
                LocalTime endTime = startTime.plusHours(1); // End time is start time + 1 hour
                bookedHours.add(new LocalTime[]{startTime, endTime});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookedHours;
    }

    public static int getTrainerScheduleID(int trainerID) {
        Connection conn = Main.dbConnection;
        int scheduleID = -1; // Default value if no schedule is found

        String sql = "SELECT schedules FROM Trainer WHERE id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, trainerID);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Integer[] schedulesArray = (Integer[]) rs.getArray("schedules").getArray();
                if (schedulesArray.length > 0) {
                    scheduleID = schedulesArray[0];
                } else {
                    System.out.println("No schedule found for trainer ID: " + trainerID);
                }
            } else {
                System.out.println("Trainer not found with ID: " + trainerID);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return scheduleID;
    }

    public static boolean checkTimeOverlap(LocalTime startTime1, LocalTime endTime1, LocalTime startTime2, LocalTime endTime2) {
        return startTime1.isBefore(endTime2) && endTime1.isAfter(startTime2);
    }

    public static boolean checkWithinWorkingHours(LocalTime time, LocalTime[] workingHours) {
        return time.isAfter(workingHours[0]) && time.isBefore(workingHours[1]);
    }

    public static List<Integer> getAllTrainerIDs() {
        List<Integer> trainerIDs = new ArrayList<>();
        Connection conn = Main.dbConnection;

        String sql = "SELECT id FROM Trainer";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int trainerID = rs.getInt("id");
                trainerIDs.add(trainerID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return trainerIDs;
    }

    public static void addClientToTrainer(int trainerID, int profileID) {
        Connection conn = Main.dbConnection;

        String sql = "UPDATE Trainer SET clients = array_append(clients, ?) WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, profileID);
            pstmt.setInt(2, trainerID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void findGymProfileByName(int profileId) {
        Connection conn = Main.dbConnection;
        String sql = "SELECT p.id, p.first_name, p.last_name, p.goal_weight, p.goal_date, " +
                "h.age, h.weight, h.height, h.body_fat_percentage, h.health_conditions " +
                "FROM Profile p " +
                "JOIN Health h ON p.health_id = h.id " +
                "WHERE p.id = ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, profileId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Profile ID: " + rs.getInt("id"));
                System.out.println("First Name: " + rs.getString("first_name"));
                System.out.println("Last Name: " + rs.getString("last_name"));
                System.out.println("Goal Weight: " + rs.getInt("goal_weight"));
                System.out.println("Goal Date: " + rs.getDate("goal_date"));
                System.out.println("Age: " + rs.getInt("age"));
                System.out.println("Weight: " + rs.getFloat("weight"));
                System.out.println("Height: " + rs.getFloat("height"));
                System.out.println("Body Fat Percentage: " + rs.getFloat("body_fat_percentage"));
                System.out.println("Health Conditions: " + rs.getString("health_conditions"));
            } else {
                System.out.println("No profile found for profile ID: " + profileId);
            }
        } catch (SQLException e) {
            System.out.println("ERROR: An error occurred while finding the profile.");
            e.printStackTrace();
        }
    }

    public static void findGymProfileByNameUI(){
        Scanner scanner = Main.scanner;
        System.out.println("Enter ID of profile:");
        int id = scanner.nextInt();
        findGymProfileByName(id);
    }


    public static void changeWorkingHours(int trainerId) {
        // Scanner for user input
        Scanner scanner = Main.scanner;
        scanner.nextLine();

        System.out.println("Enter new working hours for the trainer in the format HH:MM");
        System.out.print("Start time: ");
        String startTime = scanner.nextLine();
        System.out.print("End time: ");
        String endTime = scanner.nextLine();

        System.out.println("Start Time: " + startTime + " - -  End Time:" +endTime );

        // SQL update statement
        String sql = "UPDATE Trainer SET working_hours = ? WHERE id = ?";

        Connection conn = Main.dbConnection;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set parameters using array literal
            pstmt.setObject(1, new String[] {startTime, endTime}, Types.ARRAY);
            pstmt.setInt(2, trainerId);

            // Execute update
            int rowsUpdated = pstmt.executeUpdate();

            // Check if update was successful
            if (rowsUpdated > 0) {
                System.out.println("Working hours updated successfully for trainer ID " + trainerId);
            } else {
                System.out.println("Failed to update working hours for trainer ID " + trainerId);
            }
        } catch (SQLException e) {
            System.out.println("Error updating working hours: " + e.getMessage());
        }
    }


}


