package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Schedule
{

    public static int createSchedule(String date) {
        Connection conn = Main.dbConnection;
        String sql = "INSERT INTO schedule (schedule_date) VALUES (?) RETURNING id";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("ERROR Creating Schedule");
            e.printStackTrace();
        }
        return -1; // Return -1 if an exception occurs
    }

    public static void viewSchedule(int accountId) {
        String classSql = "SELECT c.* " +
                "FROM Profile p " +
                "JOIN Schedule s ON p.schedules @> ARRAY[s.id] " +
                "JOIN Class c ON s.classes @> ARRAY[c.id] " +
                "WHERE p.id = ?";

        try (Connection connection = Main.dbConnection;
             PreparedStatement classStmt = connection.prepareStatement(classSql)) {
            classStmt.setInt(1, accountId);
            ResultSet rs = classStmt.executeQuery();

            while (rs.next()) {
                int classId = rs.getInt("id");
                Date date = rs.getDate("date");
                Time time = rs.getTime("time");
                boolean isGroup = rs.getBoolean("is_group");
                Integer roomNumber = rs.getObject("room_number", Integer.class); // Handle NULL room numbers
                int trainerId = rs.getInt("trainer_id");
                // Retrieve other class details as needed

                // Print class details
                System.out.println("Class Information:");
                System.out.println("Class ID: " + classId);
                System.out.println("Date: " + date);
                System.out.println("Time: " + time);
                System.out.println("Is Group: " + isGroup);
                if (roomNumber != null) {
                    System.out.println("Room Number: " + roomNumber);
                } else {
                    System.out.println("Room Number: N/A");
                }
                System.out.println("Trainer ID: " + trainerId);
                System.out.println("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean isScheduleIdValid( int scheduleId) {
        Connection conn = Main.dbConnection;
        String sql = "SELECT COUNT(*) FROM Schedule WHERE id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, scheduleId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // check if classId exists in the Class table
    private static boolean isClassIdValid( int classId) {

        Connection conn = Main.dbConnection;
      
        String sql = "SELECT COUNT(*) FROM Class WHERE id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, classId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void addClassToSchedule(int scheduleId, int classId) {
        Connection conn = Main.dbConnection;
        String sql = "UPDATE Schedule SET classes = array_append(classes, ?) WHERE id = ?";

        if (isClassIdValid(classId) && isScheduleIdValid(scheduleId)) {

            try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setInt(1, classId);
                preparedStatement.setInt(2, scheduleId);
                preparedStatement.executeUpdate();

            } catch (SQLException e) {

                e.printStackTrace();
                System.out.println("ERROR adding to Schedule");

            }

        } else {

            System.out.println("ID error");
        }

    }

    public static void ViewAllGroupClasses(String date) {
        System.out.println("Showing group classes for " + date);

        Connection conn = Main.dbConnection;

        String classSql = "SELECT id, time, room_number, trainer_id FROM Class WHERE date = ? AND is_group = TRUE";
        try {
            PreparedStatement classStmt = conn.prepareStatement(classSql);
            classStmt.setDate(1, java.sql.Date.valueOf(date));

            ResultSet classRs = classStmt.executeQuery();

            if (!classRs.isBeforeFirst()) {
                System.out.println("NO GROUP CLASSES FOR THE DAY");
            } else {
                System.out.println("Group Classes for " + date + ":");
                while (classRs.next()) {
                    Time classTime = classRs.getTime("time");
                    String formattedTime = classTime.toString().substring(0, 5);
                    int roomNumber = classRs.getInt("room_number");
                    int classID = classRs.getInt("id");

                    String roomStr = (roomNumber != 0) ? String.valueOf(roomNumber) : "NONE";
                    String trainerName = FitnessClass.getTrainerForClass(classRs.getInt("id"));

                    System.out.println("Time: " + formattedTime + ", Room #: " + roomStr + ", Trainer: " + trainerName + ", Class ID: " + classID);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

