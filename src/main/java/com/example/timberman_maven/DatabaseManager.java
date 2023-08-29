package com.example.timberman_maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseManager {
    private Connection connection;
    public DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/timberman", "root", "");

            //Create if not exists
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `timberman`.`highscore` ( `hTime50` INT NOT NULL , `hTime100` INT NOT NULL , `hTime200` INT NOT NULL , `hScoreEndless` INT NOT NULL )");

            //Successful connect
            main.connectionState = true;
        } catch (Exception exception) {
            System.out.println(exception.getMessage());

            File saveFile = new File("save.txt");
            //File Existence Check
            if (saveFile.exists()) {
                System.out.println("File exists");
                FileHandling.getHighScoreLocal();
                //Unsuccessful connect
                main.connectionState = false;
            }

            else {
                //If not, create a file
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream("save.txt");

                    // Create a PrintStream that uses the FileOutputStream
                    PrintStream printStream = new PrintStream(fileOutputStream);

                    // Print the numbers to the text file
                    printStream.print("50,");
                    printStream.print("100,");
                    printStream.print("200,");
                    printStream.print("0");

                    // Close the PrintStream
                    printStream.close();
                }
                catch(Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }

        }
    }
    public Connection getConnection() {
        return connection;
    }
}

