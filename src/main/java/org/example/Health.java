package org.example;
import java.sql.*;

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

}
