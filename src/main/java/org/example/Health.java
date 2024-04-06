package org.example;
import java.sql.*;
import java.util.Scanner;

public class Health
{
    public static int makeHealth(int age, float weight, float height, float bodyFatPercentage, String[] healthNotes) {
        String healthSql = "INSERT INTO Health (weight, age, height, body_fat_percentage, health_conditions) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection connection = Main.dbConnection;
            PreparedStatement healthStmt = connection.prepareStatement(healthSql,Statement.RETURN_GENERATED_KEYS);

            healthStmt.setFloat(1, weight);
            healthStmt.setInt(2, age);
            healthStmt.setFloat(3, height);
            healthStmt.setFloat(4, bodyFatPercentage);
            Array healthConditionsArray = connection.createArrayOf("text", healthNotes);
            healthStmt.setArray(5, healthConditionsArray);
            healthStmt.executeUpdate();

            ResultSet generatedKeys = healthStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to retrieve generated health ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

    }
    private static int getHealthIdForProfile(int profileId) {
        String healthIdQuery = "SELECT health_id FROM Profile WHERE id = ?";
        try {
            Connection connection = Main.dbConnection;
            PreparedStatement stmt = connection.prepareStatement(healthIdQuery);
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("health_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static void updateHealth(int healthId, int age, float weight, float height, float bodyFatPercentage, String[] healthNotes) {
        String healthSql = "UPDATE Health SET weight = ?, age = ?, height = ?, body_fat_percentage = ?, health_conditions = ? WHERE id = ?";
        try {
            Connection connection = Main.dbConnection;
            PreparedStatement healthStmt = connection.prepareStatement(healthSql);

            healthStmt.setFloat(1, weight);
            healthStmt.setInt(2, age);
            healthStmt.setFloat(3, height);
            healthStmt.setFloat(4, bodyFatPercentage);
            Array healthConditionsArray = connection.createArrayOf("text", healthNotes);
            healthStmt.setArray(5, healthConditionsArray);
            healthStmt.setInt(6, healthId);
            healthStmt.executeUpdate();

            System.out.println("Health details updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void updatehealthUI(int profileid){
        int healthid = getHealthIdForProfile(profileid);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your age:");
        int age = scanner.nextInt();
        System.out.println("Enter your weight:");
        float weight = scanner.nextFloat();
        System.out.println("Enter your height:");
        float height = scanner.nextFloat();
        System.out.println("Enter your Body Fat percentage:");
        float bodyfat = scanner.nextFloat();
        scanner.nextLine();
        System.out.println("Enter your health conditions (comma-separated list):");
        String[] healthConditions = scanner.nextLine().split(",");

        Health.updateHealth(healthid,age,weight,height,bodyfat,healthConditions);


        }



}
