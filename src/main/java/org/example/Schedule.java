package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Schedule {

    public static int createSchedule() {
        Connection conn = Main.dbConnection;
        String sql = "INSERT INTO schedule DEFAULT VALUES RETURNING id"; // Insert without specifying schedule_date

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

        Connection connection = Main.dbConnection;

        String classSql = "SELECT c.* " +
                "FROM Profile p " +
                "JOIN Schedule s ON p.schedules @> ARRAY[s.id] " +
                "JOIN Class c ON s.classes @> ARRAY[c.id] " +
                "WHERE p.id = ?";

        try (PreparedStatement classStmt = connection.prepareStatement(classSql)) {
            classStmt.setInt(1, accountId);
            ResultSet rs = classStmt.executeQuery();

            boolean classesFound = false;

            int classNum = 1;
            while (rs.next()) {
                classesFound = true;

                int classId = rs.getInt("id");
                Time time = rs.getTime("time");
                Time endTime = Time.valueOf(time.toLocalTime().plusHours(1));
                String formattedTime = time.toString().substring(0, 5);
                String formattedEndTime = endTime.toString().substring(0, 5);
                boolean isGroup = rs.getBoolean("is_group");
                Integer roomNumber = rs.getObject("room_number", Integer.class); // Handle NULL room numbers
                int trainerId = rs.getInt("trainer_id");

                if(isGroup)
                {
                    System.out.println("\n Class " + classNum++ +" :");
                    System.out.println("   Group Class with Trainer: " + Trainer.getTrainerName(trainerId) +  "   at: " + formattedTime + " - " + endTime + "  ID: " + classId );
                }else
                {
                    System.out.println("\n Class " + classNum++ +" :");
                    System.out.println("   Personal Training class with Trainer: " + Trainer.getTrainerName(trainerId) +  "   at: " + formattedTime + " - " + endTime +"  ID: " + classId  );
                }

                if (roomNumber != null)
                    System.out.println("     Room Number: " + roomNumber);


                System.out.println(" ");
            }

            if (!classesFound) {
                System.out.println("NO CLASSES IN SCHEDULE");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isScheduleIdValid(int scheduleId) {
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
    public static boolean isClassIdValid(int classId) {

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

        System.out.println("Adding class " + classId + "to sched " + scheduleId);

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
            if(!isClassIdValid(classId))
                System.out.println("INVALID CLASS");

            if(!isScheduleIdValid(scheduleId))
                System.out.println("INVALID SCHED");
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
                java.sql.Time classTime = rs.getTime("time");
                int roomNumber = rs.getInt("room_number");
                int trainerId = rs.getInt("trainer_id");

                System.out.println("    Room Number: " + roomNumber);
                System.out.println("        Class ID: " + classId);
                System.out.println("        Time: " + classTime);
                System.out.println("        Trainer ID: " + trainerId);
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

    public static void ViewAllGroupClasses()
    {
        System.out.println("Showing All group classes \n ");
        Connection conn = Main.dbConnection;

        String classSql = "SELECT id, time, room_number, trainer_id FROM Class WHERE is_group = TRUE";
        try {
            PreparedStatement classStmt = conn.prepareStatement(classSql);
            ResultSet classRs = classStmt.executeQuery();

            if (!classRs.isBeforeFirst()) {
                System.out.println("NO GROUP CLASSES FOR THE DAY");
            } else {
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

    public static boolean isClassInSchedule(int scheduleId, int classId) {

        Connection connection = Main.dbConnection;

        String sql = "SELECT id FROM Schedule WHERE id = ? AND ? = ANY(classes)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, scheduleId);
            preparedStatement.setInt(2, classId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if the class ID exists in the schedule
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of an exception
        }
    }

    public static int removeClassFromSchedule(int scheduleId, int classIdToRemove) {

        if(!isClassInSchedule(scheduleId,classIdToRemove))
        {
            System.out.println("** Class not in schedule **");
            return -1;
        }

        Connection connection = Main.dbConnection;

        String sql = "UPDATE Schedule SET classes = array_remove(classes, ?) WHERE id = ?";
        boolean check;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, classIdToRemove);
            preparedStatement.setInt(2, scheduleId);
            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated == 0) {
                System.out.println("Schedule ID not found. No rows updated.");
            }
            else {
                System.out.println("Class removed from schedule successfully.");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return 1;
    }

    public static void deleteClassFromDB(int classID) {
        Connection conn = Main.dbConnection;
        try {
            // Delete the class entry from the Class table
            String deleteClassSql = "DELETE FROM Class WHERE id = ?";
            PreparedStatement deleteClassStmt = conn.prepareStatement(deleteClassSql);
            deleteClassStmt.setInt(1, classID);
            deleteClassStmt.executeUpdate();

            // Remove the class ID from any schedules that include it
            String updateScheduleSql = "UPDATE Schedule SET classes = array_remove(classes, ?)";
            PreparedStatement updateScheduleStmt = conn.prepareStatement(updateScheduleSql);
            updateScheduleStmt.setInt(1, classID);
            updateScheduleStmt.executeUpdate();

            System.out.println("Class with ID " + classID + " has been deleted from the database.");
        } catch (SQLException e) {
            System.out.println("Error deleting class from the database.");
            e.printStackTrace();
        }
    }
}

