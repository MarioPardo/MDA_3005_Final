package org.example;

public class Schedule
{

   public static int createSchedule(Connection conn, String date) throws SQLException
   {
       String sql = "INSERT INTO schedule (date) VALUES (?) RETURNING id";

       try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
           pstmt.setDate(1, Date.valueOf(date));
           ResultSet rs = pstmt.executeQuery();

           if (rs.next()) {
               return rs.getInt("id");
           }
       }
       return -1;

   }






}
