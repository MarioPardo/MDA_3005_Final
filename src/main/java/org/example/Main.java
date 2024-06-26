package org.example;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        String databaseName = "***";
        String url = "jdbc:postgresql://localhost:5432/" + databaseName;
        String user = "postgres";
        String password = "***";
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

        while (true)
        {
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

                    try {dbConnection.close();
                    } catch(Exception e) {
                        System.out.println("Error closing connection");
                    }


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

            case 3:
                ID = Admin.AdminSignIn(username,password);
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
                    Customer.viewCustomerProfiles(custID);
                    break;
                case 2:
                    int profileID = Profile.createProfile();
                    Customer.addProfileToCustomer(profileID,custID);
                    break;
                case 3:
                    int profID = Customer.validateProfileOwnership(custID);
                    if(profID == -1)
                        break;

                    ProfileUI(profID);

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
            System.out.println("6. Reschedule class");
            System.out.println("7. Change health");
            System.out.println("8. View Routines");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    Schedule.viewSchedule(profileID);
                    break;
                case 2:
                    Schedule.ViewAllGroupClasses();
                    break;
                case 3:
                    Profile.addGroupClassToSchedule(profileID);
                    break;
                case 4:
                    System.out.println("Booking personal training class...");
                    Profile.BookPTClass(profileID);
                    break;
                case 5:
                    System.out.println("Removing class from schedule");

                    System.out.print("Enter your class id ");
                    int classId = scanner.nextInt();
                    scanner.nextLine();

                    int trainerSchedID = Trainer.getTrainerScheduleID(FitnessClass.getTrainerIDForClass(classId));
                    int profSchedID = Profile.getProfileScheduleId( profileID);

                    Schedule.removeClassFromSchedule(profSchedID,classId);
                    Schedule.removeClassFromSchedule(trainerSchedID,classId);
                    Schedule.deleteClassFromDB(classId);

                    break;
                case 6:
                    System.out.println("Rescheduling Class");

                    System.out.print("Enter your class id ");
                    int clasId = scanner.nextInt();
                    scanner.nextLine();

                    int profileSchedID = Profile.getProfileScheduleId( profileID);
                    int tSchedID = Trainer.getTrainerScheduleID(FitnessClass.getTrainerIDForClass(clasId));

                    Schedule.removeClassFromSchedule(profileSchedID,clasId);
                    Schedule.removeClassFromSchedule(tSchedID,clasId);
                    Schedule.deleteClassFromDB(clasId);

                    System.out.println(" \n Removed Old Class, now let's book the new one \n");

                    Profile.BookPTClass(profileID);
                    break;
                case 7:
                    Health.updatehealthUI(profileID);
                    break;
                case 8:
                    System.out.println("Printing all Routines");
                    Profile.showProfileRoutines(profileID);
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
            System.out.println("3. Find gym profile by ID");
            System.out.println("4. Create new Routine");
            System.out.println("5. Show all My Routines");
            System.out.println("6. Add Routine to Client");
            System.out.println("7. View my Schedule");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    Trainer.changeWorkingHours(trainerID);
                    break;
                case 2:
                    Trainer.showTrainerClients(trainerID);

                    break;
                case 3:
                    Trainer.findGymProfileByNameUI();
                    break;
                case 4:
                    Trainer.createTrainerRoutine(trainerID);
                    break;
                case 5:
                    Trainer.showTrainerRoutines(trainerID);
                    break;
                case 6:
                    Trainer.addRoutineToProfile();
                    break;
                case 7:
                    Trainer.printTrainerSchedule(trainerID);
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
            System.out.println("5. View booked rooms");
            System.out.println("6. Cancel class ");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    Schedule.ViewAllGroupClasses();
                    break;
                case 2:
                    FitnessClass.createclassUI();
                    break;
                case 3:
                    System.out.println(" -- Updating a class -- !");
                    FitnessClass.updateclassUI();
                    break;
                case 4:
                    EquipMaintenanceUI();
                    break;
                case 5:
                    Schedule.viewAllBookedRooms();
                    break;
                case 6:
                    Schedule.cancelClassUI();
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



    public static void EquipMaintenanceUI() {
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
                    Admin.viewAllRepairTickets();
                    System.out.println("Viewing all repair tickets...");
                    break;
                case 2:
                    Admin.addRepairTicketUI();
                    break;
                case 3:
                    Admin.removeRepairTicketUI();
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