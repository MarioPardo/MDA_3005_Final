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
                throw new SQLException("Failed to retrieve generated ID.");
            }


        } catch (SQLException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return -1;
        }
    }
}


