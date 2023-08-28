package com.example.timberman_maven;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.util.Scanner;
import javax.swing.JOptionPane;

import static com.example.timberman_maven.main.*;

public class FileHandling {
    static File saveFile = new File("save.txt");
    protected static void getHighScoreLocal()
    {
        try(Scanner reader = new Scanner(saveFile))
        {

            while (reader.hasNext())
            {
                String line = reader.nextLine();
                String[] items = line.split(",");
                main.hTime50=Integer.parseInt(items[0]);
                main.hTime100=Integer.parseInt(items[1]);
                main.hTime200=Integer.parseInt(items[2]);
                main.hScoreEndless=Integer.parseInt(items[3]);
            }

        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    protected static void setHighScoreLocal(int timeInt)
    {
        if (targetScore == 50) {
            if (timeInt < hTime50) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream("save.txt");

                    // Create a PrintStream that uses the FileOutputStream
                    PrintStream printStream = new PrintStream(fileOutputStream);

                    // Print the numbers to the text file
                    printStream.print(timeInt+",");
                    printStream.print(hTime100+",");
                    printStream.print(hTime200+",");
                    printStream.print(hScoreEndless);

                    // Close the PrintStream
                    printStream.close();

                    hTime50 = timeInt;
                }
                catch(Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }

        if (targetScore == 100) {
            if (timeInt < hTime100) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream("save.txt");

                    // Create a PrintStream that uses the FileOutputStream
                    PrintStream printStream = new PrintStream(fileOutputStream);

                    // Print the numbers to the text file
                    printStream.print(hTime50+",");
                    printStream.print(timeInt+",");
                    printStream.print(hTime200+",");
                    printStream.print(hScoreEndless);

                    // Close the PrintStream
                    printStream.close();

                    hTime100 = timeInt;
                }
                catch(Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }

        if (targetScore == 200) {
            if (timeInt < hTime200) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream("save.txt");

                    // Create a PrintStream that uses the FileOutputStream
                    PrintStream printStream = new PrintStream(fileOutputStream);

                    // Print the numbers to the text file
                    printStream.print(hTime50+",");
                    printStream.print(hTime100+",");
                    printStream.print(timeInt+",");
                    printStream.print(hScoreEndless);

                    // Close the PrintStream
                    printStream.close();

                    hTime200 = timeInt;
                }
                catch(Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }

        if (targetScore != 50 && targetScore != 100 && targetScore != 200){
            if (currentScore > hScoreEndless) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream("save.txt");

                    // Create a PrintStream that uses the FileOutputStream
                    PrintStream printStream = new PrintStream(fileOutputStream);

                    // Print the numbers to the text file
                    printStream.print(hTime50+",");
                    printStream.print(hTime100+",");
                    printStream.print(hTime200+",");
                    printStream.print(currentScore);

                    // Close the PrintStream
                    printStream.close();

                    hScoreEndless = currentScore;
                }
                catch(Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    protected static void resetRecordLocal()
    {
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

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert .setTitle("Success");
            alert .setContentText("Record reset");
            alert .showAndWait();

            hTime50 = 50;
            hTime100 = 100;
            hTime200 = 200;
            hScoreEndless = 0;

        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    protected static void SyncData()
    {
        try(Scanner reader = new Scanner(saveFile))
        {

            while (reader.hasNext())
            {
                String line = reader.nextLine();
                String[] items = line.split(",");

                if (hTime50 != Integer.parseInt(items[0]) || hTime100 != Integer.parseInt(items[1]) || hTime200 != Integer.parseInt(items[2]) || hScoreEndless != Integer.parseInt(items[3]))
                {
                    int choice = JOptionPane.showConfirmDialog(
                            null,
                            "Local Data seem outdated. Do you wan to sync?",
                            "Confirmation",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (choice == JOptionPane.YES_OPTION) {
                        System.out.println("User chose YES.");
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream("save.txt");

                            // Create a PrintStream that uses the FileOutputStream
                            PrintStream printStream = new PrintStream(fileOutputStream);

                            // Print the numbers to the text file
                            printStream.print(hTime50+",");
                            printStream.print(hTime100+",");
                            printStream.print(hTime200+",");
                            printStream.print(hScoreEndless);

                            // Close the PrintStream
                            printStream.close();

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Complete");
                            alert.setContentText("Sync Complete");
                            alert.showAndWait();

                        }
                        catch(Exception e)
                        {
                            System.out.println(e.getMessage());
                        }

                    } else if (choice == JOptionPane.NO_OPTION) {
                        System.out.println("User chose NO.");
                    } else {
                        System.out.println("User closed the dialog.");
                    }
                }



            }



        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }


    }
}
