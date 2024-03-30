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
        String routineName = scanner.nextLine();

        scanner.nextLine();

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



}
