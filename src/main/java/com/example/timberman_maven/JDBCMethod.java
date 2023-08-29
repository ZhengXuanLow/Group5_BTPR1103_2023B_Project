package com.example.timberman_maven;

import javafx.scene.control.Alert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static com.example.timberman_maven.main.*;

public class JDBCMethod {

    static DatabaseManager database = new DatabaseManager();

    //Get High Score from DB
    protected static void getHighScoreDB()
    {
            try {
                Statement statement = database.getConnection().createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT hTime50, hTime100, hTime200, hScoreEndless FROM highscore");

                while (resultSet.next()) {
                    main.hTime50 = resultSet.getInt("hTime50");
                    main.hTime100 = resultSet.getInt("hTime100");
                    main.hTime200 = resultSet.getInt("hTime200");
                    main.hScoreEndless = resultSet.getInt("hScoreEndless");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

    }

    //Set High Score from DB
    protected static void setHighScoreDB(int timeInt)
    {

        if (targetScore == 50) {
            if (timeInt < hTime50) {
                try {
                    PreparedStatement preparedStatement = database.getConnection().prepareStatement("UPDATE highscore SET `hTime50`= ?");
                    preparedStatement.setInt(1, timeInt);
                    preparedStatement.executeUpdate();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        if (targetScore == 100) {
            if (timeInt < hTime100) {
                try {
                    PreparedStatement preparedStatement = database.getConnection().prepareStatement("UPDATE highscore SET `hTime100`= ?");
                    preparedStatement.setInt(1, timeInt);
                    preparedStatement.executeUpdate();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        if (targetScore == 200) {
            if (timeInt < hTime200) {
                try {
                    PreparedStatement preparedStatement = database.getConnection().prepareStatement("UPDATE highscore SET `hTime200`= ?");
                    preparedStatement.setInt(1, timeInt);
                    preparedStatement.executeUpdate();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        if (targetScore != 50 && targetScore != 100 && targetScore != 200){
            if (currentScore > hScoreEndless) {
                try {
                    PreparedStatement preparedStatement = database.getConnection().prepareStatement("UPDATE highscore SET `hScoreEndless`= ?");
                    preparedStatement.setInt(1, currentScore);
                    preparedStatement.executeUpdate();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

    }


    //Reset from DB
    protected static void resetRecordDB()
    {
        try
        {
            Statement statement = database.getConnection().createStatement();
            statement.executeUpdate("UPDATE highscore SET hTime50 = 50, hTime100 = 100, hTime200 = 200, hScoreEndless = 0;");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert .setTitle("Success");
            alert .setContentText("Record reset");
            alert .showAndWait();
            //Get again to redefine the value
            getHighScoreDB();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }


}

