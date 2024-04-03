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

    public static List<String> getClassesForAccount(int accountId) {
        List<String> classList = new ArrayList<>();
        String classSql = "SELECT time FROM Class " +
                "JOIN Schedule ON Class.id = ANY(Schedule.classes) " +
                "JOIN Trainer ON Class.trainer_id = Trainer.id " +
                "JOIN Profile ON Profile.id = ANY(Trainer.clients) " +
                "WHERE Profile.id = ?";

        try (Connection connection = Main.dbConnection;
             PreparedStatement classStmt = connection.prepareStatement(classSql)) {
            classStmt.setInt(1, accountId);
            ResultSet rs = classStmt.executeQuery();

            while (rs.next()) {
                String classTime = rs.getTime("time").toString(); // Get the time and convert it to string
                classList.add(classTime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return classList;
    }







}

