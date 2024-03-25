package org.example;

import java.sql.Connection;
import java.sql.DriverManager;

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
        String databaseName = "****";
        String url = "jdbc:postgresql://localhost:5432/" + databaseName;
        String user = "postgres";
        String password = "****";
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
        System.out.println(" *** Welcome to the MDA Fitness Club  *** ");
    }
}