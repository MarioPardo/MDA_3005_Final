package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Customer
{

    public static int customerSignIn(String username, String password)
    {
        Connection conn = Main.dbConnection;

        String sql = "SELECT id FROM Customer WHERE username = ? AND password = ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            System.out.println(pstmt);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int customerId = rs.getInt("id");
                System.out.println("Login successful!");
                return customerId;
            } else {
                System.out.println("Incorrect username or password.");
                return -1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if an error occurs
    }

    public static void addProfileToCustomer(int profileId, int customerId) {
        Connection conn = Main.dbConnection;

        String sql = "UPDATE Customer SET profile_ids = array_append(profile_ids, ?) WHERE id = ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, profileId);
            pstmt.setInt(2, customerId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Profile added to customer successfully.");
            } else {
                System.out.println("Failed to add profile to customer.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //Display all customer profiles
}
