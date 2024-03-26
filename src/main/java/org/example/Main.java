package org.example;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    static Connection dbConnection;

    public static void main(String[] args)
    {
        if(!(setDbConnection()))
            return;

        launch();


    }

    public static boolean setDbConnection()
    {
        //<editor-fold desc=" Insert Info HERE!">
        String databaseName = "finalproject";
        String url = "jdbc:postgresql://localhost:5432/" + databaseName;
        String user = "postgres";
        String password = "xsixteen123";
        //</editor-fold

        try{
            Class.forName("org.postgresql.Driver");
            dbConnection = DriverManager.getConnection(url,user,password);
        }catch(Exception e) {
            System.out.println("Problem interacting with Database! " + e.toString());
            return false;
        }
        //We are now connected
        return true;
    }



    public static void launch()
    {
        Scanner scanner = new Scanner(System.in);

        System.out.println(" *** Welcome to the MDA Fitness Club  *** ");

        while (true) {
            System.out.println("Select an option:");
            System.out.println("1. Customer Sign In");
            System.out.println("2. Trainer Sign In");
            System.out.println("3. General Management");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Customer sign in selected");

                    System.out.println("Enter your first name:");
                    String firstName = scanner.nextLine();
                    scanner.nextLine();

                    System.out.println("Enter your last name:");
                    String lastName = scanner.nextLine();

                    System.out.println("Enter your age:");
                    int age = scanner.nextInt();

                    System.out.println("Enter your weight:");
                    float weight = scanner.nextFloat();

                    System.out.println("Enter your height:");
                    float height = scanner.nextFloat();

                    System.out.println("Enter your body fat percentage:");
                    float bodyFatPercentage = scanner.nextFloat();
                    scanner.nextLine();

                    System.out.println("Enter your health conditions (comma-separated list):");
                    String[] healthConditions = scanner.nextLine().split(",");

                    System.out.println("Enter your goal weight:");
                    int goalWeight = scanner.nextInt();

                    System.out.println("Enter your goal date (YYYY-MM-DD):");
                    String goalDateStr = scanner.next();
                    java.sql.Date goalDate = java.sql.Date.valueOf(goalDateStr);

                    Profile.addProfile(firstName, lastName, age, height, weight, bodyFatPercentage, healthConditions, goalWeight, goalDate);
                    break;


                //do stuff

                case 2:
                    System.out.println("Trainer sign in selected");
                    //do stuff
                    break;
                case 3:
                    System.out.println("General Management selected");
                    //do stuff
                    break;
                case 0:
                    System.out.println("Exiting application");
                    return;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }






    }
}