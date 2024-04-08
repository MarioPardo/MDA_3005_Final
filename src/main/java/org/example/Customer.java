package org.example;

import java.sql.*;
import java.util.Scanner;

public class Customer {

    public static int customerSignIn(String username, String password) {
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


    public static void viewCustomerProfiles(int customerId) {

        Connection conn = Main.dbConnection;
        String customerSql = "SELECT profile_ids FROM Customer WHERE id = ?";
        String profileSql = "SELECT id, first_name, last_name FROM Profile WHERE id = ?";

        try {
            PreparedStatement customerStmt = conn.prepareStatement(customerSql);
            customerStmt.setInt(1, customerId);
            ResultSet customerRs = customerStmt.executeQuery();

            if (customerRs.next()) {
                Array profileIdsArray = customerRs.getArray("profile_ids");
                Integer[] profileIds = (Integer[]) profileIdsArray.getArray();

                if (profileIds.length == 0) {
                    System.out.println("NO PROFILES FOR THIS CUSTOMER");
                    return;
                }

                System.out.println("Customer's Profiles:");
                for (int profileId : profileIds) {
                    PreparedStatement profileStmt = conn.prepareStatement(profileSql);
                    profileStmt.setInt(1, profileId);
                    ResultSet profileRs = profileStmt.executeQuery();

                    if (profileRs.next()) {
                        String firstName = profileRs.getString("first_name");
                        String lastName = profileRs.getString("last_name");
                        System.out.println("ID: " + profileId + " --  Name: " + firstName + " " + lastName);
                    }
                }
            } else {
                System.out.println("Customer not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
