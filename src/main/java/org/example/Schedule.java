package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
    public static void viewAllBookedRooms() {
        Connection conn = Main.dbConnection;
        String sql = "SELECT * FROM Class WHERE room_number IS NOT NULL";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            boolean foundBookings = false;
            System.out.println("All Booked Rooms:");
            while (rs.next()) {
                foundBookings = true;
                int classId = rs.getInt("id");
                java.sql.Date classDate = rs.getDate("date");
                java.sql.Time classTime = rs.getTime("time");
                int roomNumber = rs.getInt("room_number");
                int trainerId = rs.getInt("trainer_id");

                System.out.println("Class ID: " + classId);
                System.out.println("Date: " + classDate);
                System.out.println("Time: " + classTime);
                System.out.println("Room Number: " + roomNumber);
                System.out.println("Trainer ID: " + trainerId);
                System.out.println();
            }
            if (!foundBookings) {
                System.out.println("No booked rooms found.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: An error occurred while fetching booked rooms.");
            e.printStackTrace();
        }
    }
    private static void cancelClass(int classId) {
        Connection conn = Main.dbConnection;
        String deleteFromClassSql = "DELETE FROM Class WHERE id = ?";
        String removeFromScheduleSql = "UPDATE Schedule SET classes = array_remove(classes, ?) WHERE ? = ANY(classes)";
        String removeFromProfileSql = "UPDATE Profile SET schedules = array_remove(schedules, ?) WHERE ? = ANY(schedules)";

        try {
            // Remove the class from the Class table
            PreparedStatement deleteFromClassStmt = conn.prepareStatement(deleteFromClassSql);
            deleteFromClassStmt.setInt(1, classId);
            int rowsAffected = deleteFromClassStmt.executeUpdate();

            if (rowsAffected > 0) {
                // Remove the class from the Schedule table
                PreparedStatement removeFromScheduleStmt = conn.prepareStatement(removeFromScheduleSql);
                removeFromScheduleStmt.setInt(1, classId);
                removeFromScheduleStmt.setInt(2, classId);
                removeFromScheduleStmt.executeUpdate();

                // Remove the class from profiles associated with the class
                PreparedStatement removeFromProfileStmt = conn.prepareStatement(removeFromProfileSql);
                removeFromProfileStmt.setInt(1, classId);
                removeFromProfileStmt.setInt(2, classId);
                removeFromProfileStmt.executeUpdate();

                System.out.println("Class canceled successfully.");
            } else {
                System.out.println("No class found with the given ID.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: An error occurred while canceling the class.");
            e.printStackTrace();
        }
    }
    public static void cancelClassUI(){
        Scanner scanner = Main.scanner;
        System.out.println("Enter id of class:");
        int id = scanner.nextInt();
        cancelClass(id);
    }

}

