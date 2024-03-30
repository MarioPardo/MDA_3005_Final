package org.example;

import java.sql.*;
import java.util.Arrays;

public class Trainer
{

    public static void createTrainerRoutine(int trainerID)
    {
        int routineID = Routine.createRoutine();

        if(routineID == -1)
            return;

        addRoutineToTrainer(trainerID, routineID);
    }

    public static void addRoutineToTrainer(int trainerID, int routineID) {
        Connection conn = Main.dbConnection;

        int[] currentRoutines = getTrainerRoutines(trainerID);

        int[] updatedRoutines = Arrays.copyOf(currentRoutines, currentRoutines.length + 1);
        updatedRoutines[currentRoutines.length] = routineID;

        updateTrainerRoutines(trainerID, updatedRoutines);

        System.out.println("Routine with ID " + routineID + " added to the trainer with ID " + trainerID + " successfully.");
    }

    private static int[] getTrainerRoutines(int trainerID) {
        Connection conn = Main.dbConnection;
        try {
            String sql = "SELECT routines FROM Trainer WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, trainerID);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    Array routinesArray = rs.getArray("routines");
                    if(routinesArray == null)
                        routinesArray = conn.createArrayOf("INTEGER", new Integer[0]);
                    Integer[] routines = (Integer[]) routinesArray.getArray();
                    return Arrays.stream(routines).mapToInt(Integer::intValue).toArray();
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to retrieve current routines.");
            e.printStackTrace();
        }
        return new int[0];
    }

    private static void updateTrainerRoutines(int trainerID, int[] updatedRoutines)
    {
        Connection conn = Main.dbConnection;
        try {
            String sql = "UPDATE Trainer SET routines = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setArray(1, conn.createArrayOf("INTEGER", Arrays.stream(updatedRoutines).boxed().toArray()));
                pstmt.setInt(2, trainerID);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to update trainer routines.");
            e.printStackTrace();
        }
    }
}
