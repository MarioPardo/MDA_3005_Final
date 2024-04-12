package org.example;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import java.sql.*;

public class FitnessClass
{

    public static int createClass(Time time, boolean isGroup, Integer roomNumber, int trainerId, Integer[] participants) {
        String insertClassSql = "INSERT INTO Class ( time, is_group, room_number, trainer_id, participants) " +
                "VALUES (?, ?, ?, ?, ?)";
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
        Scanner scanner = Main.scanner;
        System.out.println("Enter Class ID to update: ");
        int classId = scanner.nextInt();

        System.out.println("Enter Time (HH:MM:SS): ");
        String timeStr = scanner.next();
        Time time = Time.valueOf(timeStr);

        System.out.println("Enter Room Number (if applicable, otherwise enter null): ");
        Integer roomNumber = null;
        String roomNumberStr = scanner.next();
        if (!roomNumberStr.equalsIgnoreCase("null")) {
            roomNumber = Integer.parseInt(roomNumberStr);
        }
        System.out.println("Enter Trainer ID: ");
        int trainerId = scanner.nextInt();

        if(!Trainer.checkTrainerExists(trainerId))
        {
            System.out.println("Trainer does not exist!");
            return;
        }

        Integer[] participants;
        participants = new Integer[0];

        updateClass(classId, time, true, roomNumber, trainerId, participants);
    }
    public static void createclassUI(){
        Scanner scanner = Main.scanner;

        // Select an hour slot
        LocalTime time = null;
        System.out.println("Enter a time in the format HH:MM:");
        String inputTime = scanner.nextLine();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
            time = LocalTime.parse(inputTime, formatter);
            System.out.println("Time entered: " + time);
        } catch (Exception e) {
            System.out.println("Invalid time format. Please enter in HH:MM format.");
            return;
        }

        // Convert to SQL format
        Time sqlTime = Time.valueOf(time); // Directly convert LocalTime to Time


        System.out.println("Enter room number (optional, enter 'null' if not applicable):");
        Integer roomNumber = null;
        String roomNumberStr = scanner.nextLine();
        if (!roomNumberStr.equalsIgnoreCase("null")) {
            roomNumber = Integer.parseInt(roomNumberStr);
        }

        Integer[] participants;
        participants = new Integer[0];

        System.out.println("Enter Trainer ID:");
        int trainerId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if(!Trainer.checkTrainerExists(trainerId))
        {
            System.out.println("Trainer does not exist!");
            return;
        }

        LocalTime[] workingHours = Trainer.getTrainerWorkingHours(trainerId);
        List<LocalTime[]> bookedHours = Trainer.getTrainerBookedHours(trainerId);

        if(!Trainer.checkWithinWorkingHours(time, workingHours))
        {
            System.out.println("This time does not fit within the trainer's working hours");
            return;
        }

        for (LocalTime[] hours : bookedHours) {
            if (Trainer.checkTimeOverlap(time, time.plusHours(1), hours[0], hours[1])) {
                System.out.println("This time overlaps with a booked class");
                return;
            }
        }

        // Call createClass function with user inputted data
        FitnessClass.createClass(sqlTime, true, roomNumber, trainerId, participants);
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

    public static int getTrainerIDForClass(int classID) {
        Connection conn = Main.dbConnection;
        int trainerID = -1; // Initialize with a default value in case no trainer is found

        String trainerSql = "SELECT trainer_id FROM Class WHERE id = ?";
        try {
            PreparedStatement trainerStmt = conn.prepareStatement(trainerSql);
            trainerStmt.setInt(1, classID);
            ResultSet trainerRs = trainerStmt.executeQuery();

            if (trainerRs.next()) {
                trainerID = trainerRs.getInt("trainer_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trainerID;
    }




}


