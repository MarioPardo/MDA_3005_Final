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
}
