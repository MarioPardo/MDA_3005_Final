package org.example;

import java.sql.*;
import java.util.Scanner;

public class Admin {
    public static void addRepairTicket(String issueDescription, Date ticketDate) {
        String sql = "INSERT INTO RepairTicket (issue_description, ticket_date) VALUES (?, ?)";

        try {
            Connection connection = Main.dbConnection;
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, issueDescription);
            statement.setDate(2, new java.sql.Date(ticketDate.getTime()));
            //statement.setString(3, status);

            statement.executeUpdate();

            System.out.println("Repair ticket added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addRepairTicketUI(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter issue description:");
        String issueDescription = scanner.nextLine();

        System.out.println("Enter ticket date (YYYY-MM-DD):");
        String ticketDateStr = scanner.nextLine();
        Date ticketDate = Date.valueOf(ticketDateStr);

//        System.out.println("Enter status:");
//        String status = scanner.nextLine();

        // Now that you have collected user input, call the method to add the repair ticket
        addRepairTicket(issueDescription, ticketDate);
    }

    public static void removeRepairTicket(int ticketId) {
        String sql = "DELETE FROM RepairTicket WHERE ticket_id = ?";

        try {
            Connection connection = Main.dbConnection;
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, ticketId);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Repair ticket removed successfully.");
            } else {
                System.out.println("No repair ticket found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void removeRepairTicketUI(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the ID of the ticket you wish to remove:");
        int ticketId = scanner.nextInt();
        removeRepairTicket(ticketId);
    }

    public static void viewAllRepairTickets() {
        Connection conn = Main.dbConnection;
        String sql = "SELECT * FROM RepairTicket";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            boolean foundTickets = false;
            System.out.println("All Repair Tickets:");
            while (rs.next()) {
                foundTickets = true;
                int ticketId = rs.getInt("ticket_id");
                String issueDescription = rs.getString("issue_description");
                java.sql.Date ticketDate = rs.getDate("ticket_date");
                //String status = rs.getString("status");

                System.out.println("Ticket ID: " + ticketId);
                System.out.println("Issue Description: " + issueDescription);
                System.out.println("Ticket Date: " + ticketDate);
                //System.out.println("Status: " + status);
                System.out.println();
            }
            if (!foundTickets) {
                System.out.println("No repair tickets found.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: An error occurred while fetching repair tickets.");
            e.printStackTrace();
        }
    }

    public static int AdminSignIn(String username, String password)
    {
        String adminUsername = "admin1";
        String adminPassword = "adminpass1";

        if(!username.equals(adminUsername))
        {
            System.out.println(" ** Incorrect Username ** \n");
            return -1;
        }
        if(!password.equals(adminPassword))
        {
            System.out.println(" ** Incorrect Password ** \n");
            return -1;
        }

        return 1;
    }

    public static boolean PayForClass() {
        Scanner scanner = Main.scanner;

        System.out.println("\n PROCESSING PAYMENT ... \n");
        System.out.println("Charging to credit card on file. Please confirm (Y/N): ");

        String userInput = scanner.nextLine().trim().toUpperCase(); // Get user input and convert to uppercase

        // Check user input
        if (userInput.equals("Y")) {
            System.out.println(" * Payment Confirmed ! * ");
            return true;
        } else if (userInput.equals("N")) {
            System.out.println(" * PAYMENT DENIED * ");
            return false;
        } else {
            System.out.println("Invalid input. Please enter Y or N.");
            return PayForClass(); // Recursively call the function to prompt the user again
        }
    }


}
