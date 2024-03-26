package org.example;
import java.sql.*;


public class Profile {

    public static void addProfile(String fname, String lname, int age, float height, float weight, float bodyFatPercentage, String[] healthNotes, int weightGoal, Date goalDeadline) {
        String profileSql = "INSERT INTO Profile (first_name, last_name, goal_weight, goal_date, health_id) VALUES (?, ?, ?, ?,?)";
       int healthDetails = Health.makeHealth(age, weight, height, bodyFatPercentage, healthNotes);
       if(healthDetails == -1){
           System.out.println("Health details have not been added. Please check the information you have entered.");
           return;
       }
        try{
            Connection connection = Main.dbConnection;
            PreparedStatement profileStmt = connection.prepareStatement(profileSql, Statement.RETURN_GENERATED_KEYS);

            profileStmt.setString(1, fname);
            profileStmt.setString(2, lname);
            profileStmt.setInt(3, weightGoal);
            profileStmt.setDate(4, goalDeadline);
            profileStmt.setInt(5,healthDetails);
            profileStmt.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

