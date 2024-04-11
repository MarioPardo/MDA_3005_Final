package org.example;
import java.sql.*;
import java.util.Scanner;

import java.sql.*;

public class FitnessClass
{

    public static int createClass( Time time, boolean isGroup, Integer roomNumber, int trainerId, Integer[] participants) {
        String insertClassSql = "INSERT INTO Class (time, is_group, room_number, trainer_id, participants) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String updateScheduleSql = "UPDATE Schedule SET classes = array_append(classes, ?) WHERE id = (SELECT schedules[1] FROM Trainer WHERE id = ?)";

        try {
            Connection conn = Main.dbConnection;

            // Insert class into Class table
            PreparedStatement insertStatement = conn.prepareStatement(insertClassSql, Statement.RETURN_GENERATED_KEYS);
            insertStatement.setTime(1, time);
            insertStatement.setBoolean(2, isGroup);

            if (roomNumber != null)
                insertStatement.setInt(3, roomNumber);
            else
                insertStatement.setNull(3, Types.INTEGER);

            insertStatement.setInt(4, trainerId);
            insertStatement.setArray(5, conn.createArrayOf("INTEGER", participants));
            int rowsInserted = insertStatement.executeUpdate();

            // Check if class was inserted successfully
            if (rowsInserted <= 0) {
                throw new SQLException("Failed to insert class.");
            }

            // Get the ID of the newly created class
            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("Failed to retrieve generated ID.");
            }
            int classId = generatedKeys.getInt(1);

            // Update schedule's classes array
            PreparedStatement updateStatement = conn.prepareStatement(updateScheduleSql);
            updateStatement.setInt(1, classId);
            updateStatement.setInt(2, trainerId);
            int rowsUpdated = updateStatement.executeUpdate();

            // Check if schedule was updated successfully
            if (rowsUpdated <= 0) {
                throw new SQLException("Failed to update schedule.");
            }

            System.out.println("A new class has been created and added to the schedule.");
            return classId;
        } catch (SQLException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return -1;
        }
    }

    private static void updateClass(int classId, Time time, boolean isGroup, Integer roomNumber, int trainerId, Integer[] participants) {
        String sql = "UPDATE CLASS SET time=?,is_group=?,room_number=?,trainer_id=?,participants=? WHERE id=?";
        try {
            Connection conn = Main.dbConnection;
            PreparedStatement statement = conn.prepareStatement(sql);

            // Set values for parameters
            statement.setTime(1, time);
            statement.setBoolean(2, isGroup);
            if (roomNumber != null)
                statement.setInt(3, roomNumber);
            else
                statement.setNull(3, Types.INTEGER);
            statement.setInt(4, trainerId);
            statement.setArray(5, conn.createArrayOf("INTEGER", participants));
            statement.setInt(6, classId);

            // Execute the statement
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Class updated successfully.");
            } else {
                System.out.println("No class found with ID: " + classId);
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while updating class.");
            e.printStackTrace();
        }
    }
    public static void updateclassUI(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Class ID to update: ");
        int classId = scanner.nextInt();

        System.out.println("Enter Time (HH:MM:SS): ");
        String timeStr = scanner.next();
        Time time = Time.valueOf(timeStr);

        System.out.println("Is it a group class? (true/false): ");
        boolean isGroup = scanner.nextBoolean();

        System.out.println("Enter Room Number (if applicable, otherwise enter null): ");
        Integer roomNumber = null;
        String roomNumberStr = scanner.next();
        if (!roomNumberStr.equalsIgnoreCase("null")) {
            roomNumber = Integer.parseInt(roomNumberStr);
        }
        System.out.println("Enter Trainer ID: ");
        int trainerId = scanner.nextInt();

        System.out.println("Enter Participant IDs (comma-separated, leave empty if none): ");
        scanner.nextLine();
        Integer[] participants;
        String participantsStr = scanner.nextLine();
        if (participantsStr.isEmpty()) {
            participants = new Integer[0];
        } else {
            String[] participantsArr = participantsStr.split(",");
            participants = new Integer[participantsArr.length];
            for (int i = 0; i < participantsArr.length; i++) {
                participants[i] = Integer.parseInt(participantsArr[i]);
            }
        }
        updateClass(classId, time, isGroup, roomNumber, trainerId, participants);
    }
    public static void createclassUI(){
        Scanner scanner = new Scanner(System.in);

        // Prompt user for class details
        System.out.println("Enter date (YYYY-MM-DD):");
        String dateStr = scanner.nextLine();
        Date date = Date.valueOf(dateStr);

        System.out.println("Enter time (HH:MM:SS):");
        String timeStr = scanner.nextLine();
        Time time = Time.valueOf(timeStr);

        System.out.println("Is it a group class? (true/false):");
        boolean isGroup = scanner.nextBoolean();
        scanner.nextLine(); // Consume newline

        System.out.println("Enter room number (optional, enter 'null' if not applicable):");
        Integer roomNumber = null;
        String roomNumberStr = scanner.nextLine();
        if (!roomNumberStr.equalsIgnoreCase("null")) {
            roomNumber = Integer.parseInt(roomNumberStr);
        }

        System.out.println("Enter Participant IDs (comma-separated, leave empty if none): ");
        scanner.nextLine();
        Integer[] participants;
        String participantsStr = scanner.nextLine();
        if (participantsStr.isEmpty()) {
            participants = new Integer[0];
        } else {
            String[] participantsArr = participantsStr.split(",");
            participants = new Integer[participantsArr.length];
            for (int i = 0; i < participantsArr.length; i++) {
                participants[i] = Integer.parseInt(participantsArr[i]);
            }
        }

        System.out.println("Enter Trainer ID:");
        int trainerId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Call createClass function with user inputted data
        FitnessClass.createClass(time, isGroup, roomNumber, trainerId, participants);
    }

    public static String getTrainerForClass(int classID)
    {
        Connection conn = Main.dbConnection;

        String trainerSql = "SELECT first_name, last_name FROM Trainer WHERE id = (SELECT trainer_id FROM Class WHERE id = ?)";
        try {
            PreparedStatement trainerStmt = conn.prepareStatement(trainerSql);
            trainerStmt.setInt(1, classID);
            ResultSet trainerRs = trainerStmt.executeQuery();

            if (trainerRs.next()) {
                String firstName = trainerRs.getString("first_name");
                String lastName = trainerRs.getString("last_name");
                return firstName + " " + lastName;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "UNKNOWN TRAINER";
    }




}


