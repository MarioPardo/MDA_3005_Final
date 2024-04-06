package org.example;
import java.sql.*;
import java.util.Scanner;

import java.sql.*;

public class FitnessClass
{

    public static int createClass(Date date, Time time, boolean isGroup, Integer roomNumber, int trainerId, Integer[] participants) {
        String sql = "INSERT INTO Class (date, time, is_group, room_number, trainer_id, participants) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try {

            Connection conn = Main.dbConnection;
            PreparedStatement statement = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            // Prepare the SQL statement


            // Set values for parameters
            statement.setDate(1, date);
            statement.setTime(2, time);
            statement.setBoolean(3, isGroup);
            if (roomNumber != null)
                statement.setInt(4, roomNumber);
            else
                statement.setNull(4, Types.INTEGER);
            statement.setInt(5, trainerId);
            statement.setArray(6, conn.createArrayOf("INTEGER", participants));

            // Execute the statement
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new row has been inserted successfully.");
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to retrieve generated ID.");
            }


        } catch (SQLException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return -1;
        }
    }
    private static void updateClass(int classId,Date date, Time time, boolean isGroup, Integer roomNumber, int trainerId, Integer[] participants) {
        String sql = "UPDATE CLASS SET date=?,time=?,is_group=?,room_number=?,trainer_id=?,participants=? WHERE id=?";
        try {
            Connection conn = Main.dbConnection;
            PreparedStatement statement = conn.prepareStatement(sql);
            // Prepare the SQL statement

            // Set values for parameters
            statement.setDate(1, date);
            statement.setTime(2, time);
            statement.setBoolean(3, isGroup);
            if (roomNumber != null)
                statement.setInt(4, roomNumber);
            else
                statement.setNull(4, Types.INTEGER);
            statement.setInt(5, trainerId);
            statement.setArray(6, conn.createArrayOf("INTEGER", participants));
            statement.setInt(7, classId);

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

        System.out.println("Enter Date (YYYY-MM-DD): ");
        String dateStr = scanner.next();
        Date date = Date.valueOf(dateStr);

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
        updateClass(classId, date, time, isGroup, roomNumber, trainerId, participants);
    }
}


