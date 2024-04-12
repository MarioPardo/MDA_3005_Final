This is our submission for COMP3005 Final Project V2

Running Instructions:
    We used a postgress database.
    To create the database, please run the DatabaseCreation.sql code
    And then to add data to it, FIRST run STEP 1 of the DatabasePopulation.sql (clearly labeled)
    THEN run STEP 2 of the DatabasePopulation.sql. It is necessary that this is done in two steps. This is due to foreign keys

    Once the database is done, you can connect to it in java in Main.java, in the setDBConnection function you may input the database name and password

Trying out the code:
    First you have to login to either customer, trainer, or admin.
    Here is the login for each
        Customer1:
            username: customer1    password:password1
        Customer2:
            username: customer1    password:password1

        Trainer1:
        username: michael_smith   password:password1
        Trainer2:
        username: emma_johnson    password:password2

        Admin:
           username: admin1   password:adminpass1

        In case of any issues, everything is in the database and you can Query Customer or trainer to check the login.
            for admin, it is in a function in Admin.java
