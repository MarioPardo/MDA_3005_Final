package org.example;

import java.sql.*;

public class FitnessClass
{

    public static int createClass(Date date, Time time, boolean isGroup, Integer roomNumber, int trainerId, Integer[] participants) {
        String sql = "INSERT INTO Class (date, time, is_group, room_number, trainer_id, participants) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try {

            Connection conn = Main.dbConnection;
            PreparedStatement statement = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            // Prepare the SQL statement


            // Set values for parameters
            statement.setDate(1, date);
            statement.setTime(2, time);
            statement.setBoolean(3, isGroup);
            if (roomNumber != null)
                statement.setInt(4, roomNumber);
            else
                statement.setNull(4, Types.INTEGER);
            statement.setInt(5, trainerId);
            statement.setArray(6, conn.createArrayOf("INTEGER", participants));

            // Execute the statement
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new row has been inserted successfully.");
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to retrieve generated health ID.");
            }


        } catch (SQLException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return -1;
        }
    }
}


/*

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
 */
