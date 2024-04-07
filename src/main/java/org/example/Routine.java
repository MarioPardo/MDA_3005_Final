package org.example;

import java.sql.*;
import java.util.Scanner;

public class Routine
{
    public static int createRoutine() //returns routine id
    {
        Connection conn = Main.dbConnection;
        Scanner scanner = Main.scanner;

        System.out.print("Enter the name of the routine: ");
        scanner.nextLine();
        String routineName = scanner.nextLine();

        System.out.print("Enter exercises as a comma-separated list: ");
        String exercisesInput = scanner.nextLine();

        String[] exercises = exercisesInput.split(",");

        try {
            String sql = "INSERT INTO Routine (name, exercises) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, routineName);
                pstmt.setArray(2, conn.createArrayOf("VARCHAR", exercises));

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to create routine");
            e.printStackTrace();
        }
        return -1;
    }


    public static boolean isRoutineExists(int routineID) {
        Connection conn = Main.dbConnection;
        try {
            String sql = "SELECT id FROM Routine WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, routineID);
                ResultSet rs = pstmt.executeQuery();
                return rs.next(); // If next() returns true, routine exists
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to check routine existence.");
            e.printStackTrace();
            return false;
        }
    }


}
