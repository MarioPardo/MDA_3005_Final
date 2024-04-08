package org.example;

import java.sql.*;
import java.util.Arrays;
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
            String classSql = "SELECT id, date, time, is_group, room_number, participants FROM Class WHERE trainer_id = ?";
            PreparedStatement classStmt = conn.prepareStatement(classSql);
            classStmt.setInt(1, trainerId);
            ResultSet classRs = classStmt.executeQuery();

            // Print classes
            while (classRs.next()) {
                int classId = classRs.getInt("id");
                Date classDate = classRs.getDate("date");
                Time classTime = classRs.getTime("time");
                boolean isGroup = classRs.getBoolean("is_group");
                Array participantsArray = classRs.getArray("participants");

                Integer[] participants = null;
                if(participantsArray != null)
                     participants = (Integer[]) participantsArray.getArray();

                if (isGroup) {
                    System.out.println("    * Teaching group class at " + classTime);
                } else {
                    System.out.print("    * Personal training class on " + classDate.toString() + " at " + classTime);
                    // Print participants' names
                    if(participants != null)
                        for (int participantId : participants) {
                            String participantName = getParticipantName(participantId);
                            System.out.print("  | with client " + participantName + "\n");
                        }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getParticipantName(int participantId) {
        Connection conn = Main.dbConnection;
        String participantName = "";
        try {
            String participantSql = "SELECT first_name, last_name FROM Profile WHERE id = ?";
            PreparedStatement participantStmt = conn.prepareStatement(participantSql);
            participantStmt.setInt(1, participantId);
            ResultSet participantRs = participantStmt.executeQuery();
            if (participantRs.next()) {
                String firstName = participantRs.getString("first_name");
                String lastName = participantRs.getString("last_name");
                participantName = firstName + " " + lastName;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception here (printing or logging)
            participantName = "N/A"; // Set a default value for participantName
        }
        return participantName;
    }

}
