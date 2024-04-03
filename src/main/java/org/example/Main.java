package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    static Connection dbConnection;
    static Scanner scanner;

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
        scanner = new Scanner(System.in);
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

                    int custID = SignIn(1);
                    if(custID == -1)
                        break;
                    CustomerUI(custID);

                    break;
                case 2:
                    System.out.println("Trainer sign in selected");
                    int trainerID = SignIn(1);
                    if(trainerID == -1)
                        break;

                    trainerID = 1; //for testing for now
                    TrainerUI(trainerID);

                    break;
                case 3:
                    System.out.println("General Management selected");
                    int mgmtID = SignIn(1);
                    if(mgmtID == -1)
                        break;

                    MgmtUI();

                    break;
                case 0:
                    System.out.println("Exiting application");
                    return;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }


    }

    //UI Functions
    public static int SignIn(int type)
    {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        int ID = 1;

        switch (type)
        {
            case 1: //call sql user sign  in function
                break;
            case 2:  //call sql trainer sign in function
                break;
            case 3: //management sql  sign in in function
                break;
        }

        return ID;
    }
    
    public static void CustomerUI(int custID)
    {
        System.out.println(" \n \n Welcome to your Customer profile!");

        //view all profiles

        //add profile
            //TODO link this added profile to the customer
        Profile.addProfile();

        //select profile to go to
        

    }

    public static void ProfileUI(int profileID)
    {
        //view schedule

        //view all available group classes

        //add group class to schedule

        //book personal training class

        //remove class from schedule

        //change health

    }

    public static void TrainerUI(int trainerID)
    {
        System.out.println(" \n \n Welcome to your Trainer profile!");

        while (true) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Set working hours for the day");
            System.out.println("2. Show all clients");
            System.out.println("3. Find gym profile by name");
            System.out.println("4. Create new Routine");
            System.out.println("5, Show all My Routines");
            System.out.println("6, Add Routine to Client");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    //setWorkingHours();
                    break;
                case 2:
                    //showAllClients();
                    break;
                case 3:
                   // findGymProfileByName();
                    break;
                case 4:
                    Trainer.createTrainerRoutine(trainerID);
                    break;
                case 5:
                    //Trainer.showAllRoutines();
                    break;
                case 6:
                    Trainer.addRoutineToProfile();
                    break;
                case 0:
                    System.out.println("Exiting Trainer Dashboard \n \n");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

    }

    public static void MgmtUI()
    {
        System.out.println(" \n \n Welcome to your Management dashboard!");

        //view day's schedule

        //make a group class and add to sched

        //update a class

        // Equipment management
            //1)View all repair tickets
            //2) Add repair ticket
            //3) remove repair ticket


    }
}