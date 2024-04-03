package org.example;

import java.sql.*;
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
                    int trainerID = SignIn(2);
                    if(trainerID == -1)
                        break;


                    TrainerUI(trainerID);

                    break;
                case 3:
                    System.out.println("General Management selected");
                    int mgmtID = SignIn(3);
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
        scanner.nextLine();
        String username = scanner.nextLine();
        username = username.trim();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        password = password.trim();


        int ID = -1;

        switch (type)
        {
            case 1:
                 ID = Customer.customerSignIn(username, password);
                break;
            case 2:
                 ID = Trainer.TrainerSignIn(username,password);
                break;

            case 3: //management sql  sign in in function
                //TODO make customer sign in func
                ID = 1; //temp for now

                break;
        }

        return ID;
    }


    public static void CustomerUI(int custID) {
        System.out.println("\n\nWelcome to your Customer profile!");

        int choice = -1;

        while (choice != 0) {
            System.out.println("\nChoose an option:");
            System.out.println("1. View all profiles");
            System.out.println("2. Add profile");
            System.out.println("3. Select profile to go to");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    // TODO: View customer all profiles

                    break;
                case 2:
                    Profile.addProfile(); //TODO change this func to return an id
                    // TODO: Link this added profile to the customer
                    break;
                case 3:
                    // TODO: Select profile to go to
                    break;
                case 0:
                    System.out.println("Exiting Customer profile...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

    }

    public static void ProfileUI(int profileID) {
        System.out.println("\n\nWelcome to your Profile!");

        int choice = -1;

        while (choice != 0) {
            System.out.println("\nChoose an option:");
            System.out.println("1. View schedule");
            System.out.println("2. View all available group classes");
            System.out.println("3. Add group class to schedule");
            System.out.println("4. Book personal training class");
            System.out.println("5. Remove class from schedule");
            System.out.println("6. Change health");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    // TODO: View schedule
                    break;
                case 2:
                    // TODO: View all available group classes
                    break;
                case 3:
                    // TODO: Add group class to schedule
                    break;
                case 4:
                    // TODO: Book personal training class
                    System.out.println("Booking personal training class...");
                    break;
                case 5:
                    // TODO: Remove class from schedule
                    System.out.println("Removing class from schedule...");
                    break;
                case 6:
                    // TODO : Change health
                    System.out.println("Changing health...");
                    break;
                case 0:
                    System.out.println("Exiting Profile...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
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
                    //TODO : setWorkingHours();
                    break;
                case 2:
                    //TODO: showAllClients();
                    break;
                case 3:
                   // TODO: findGymProfileByName();
                    break;
                case 4:
                    Trainer.createTrainerRoutine(trainerID);
                    break;
                case 5:
                    //TODO: Trainer.showAllRoutines();
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

    public static void MgmtUI() {
        System.out.println("\n\nWelcome to your Management dashboard!");

        int choice = -1;

        while (choice != 0) {
            System.out.println("\nChoose an option:");
            System.out.println("1. View day's schedule");
            System.out.println("2. Make a group class and add to schedule");
            System.out.println("3. Update a class");
            System.out.println("4. Equipment management");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    // TODO: View day's schedule
                    System.out.println("Viewing day's schedule...");
                    break;
                case 2:
                    // TODO: Make a group class and add to schedule
                    System.out.println("Making a group class and adding to schedule...");
                    break;
                case 3:
                    // TODO: Update a class
                    System.out.println("Updating a class...");
                    break;
                case 4:
                    EquipMaintenance();
                    break;
                case 0:
                    System.out.println("Exiting Management dashboard...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    public static void EquipMaintenance() {
        System.out.println("\nEquipment Maintenance:");

        int choice = -1;

        while (choice != 0) {
            System.out.println("\nChoose an option:");
            System.out.println("1. View all repair tickets");
            System.out.println("2. Add repair ticket");
            System.out.println("3. Remove repair ticket");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    // TODO: View all repair tickets
                    System.out.println("Viewing all repair tickets...");
                    break;
                case 2:
                    // TODO: Add repair ticket
                    System.out.println("Adding repair ticket...");
                    break;
                case 3:
                    // TODO: Remove repair ticket
                    System.out.println("Removing repair ticket...");
                    break;
                case 0:
                    System.out.println("Exiting Equipment Maintenance...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
}