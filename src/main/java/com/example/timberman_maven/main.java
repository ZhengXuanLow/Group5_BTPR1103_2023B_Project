package com.example.timberman_maven;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.util.Random;
import java.util.Scanner;


public class main extends Application { //Inheritance

    Integer[] innerArray = new Integer[7]; //Create an Array to store the int
    protected int resultTime = 0; //Store the play time
    protected static int hTime50 = 50; //Default high score of target50
    protected static int hTime100 = 100; //Default high score of target100
    protected static int hTime200 = 200; //Default high score of target200
    protected static int hScoreEndless = 0; //Default high score of targetEndLess
    protected static int currentScore = 0; //Store the current player score
    protected static int targetScore = 50; //Default target score
    protected static boolean connectionState = false; //Default connection state, true when connect to database.
    boolean gameState = false; //Default game state as false, true when the game start
    long startTime; //Store start time

    //Menu Related
    Menu settingMenu = new Menu("Setting");
    Menu helpMenu = new Menu("Help");
    MenuItem settingMenuItem = new MenuItem("Setting");
    MenuItem helpMenuItem = new MenuItem("Help");
    MenuBar menuBarTop = new MenuBar();

    //Button
    Button startGameButton = new Button("Start");
    Button backToGameButton = new Button("Back");
    Button deleteButton = new Button("Reset Record");

    //Label
    Label titleLabel = new Label("TimberMan " + "\n     Game");

    //Check is the game started, true when the game start
    boolean gameStarted = false;

    //Image Property
    Image leftTreeImage = new Image(main.class.getResource("left.png").toString());
    Image rightTreeImage = new Image(main.class.getResource("right.png").toString());
    Image noTreeImage = new Image(main.class.getResource("no.png").toString());
    Image standLeftImage = new Image(main.class.getResource("standright.png").toString());
    Image standRightImage = new Image(main.class.getResource("standleft.png").toString());
    Image chopLeftImage = new Image(main.class.getResource("chopleft.png").toString());
    Image chopRightImage = new Image(main.class.getResource("chopright.png").toString());
    Image deadLeftImage = new Image(main.class.getResource("deadleft.png").toString());
    Image deadRightImage = new Image(main.class.getResource("deadright.png").toString());
    Image overlay = new Image(main.class.getResource("overlay.png").toString());
    ImageView playerImageView = new ImageView();
    ImageView index0ImageView = new ImageView();
    ImageView index1ImageView = new ImageView();
    ImageView index2ImageView = new ImageView();
    ImageView index3ImageView = new ImageView();
    ImageView index4ImageView = new ImageView();
    ImageView index5ImageView = new ImageView();
    ImageView index6ImageView = new ImageView();
    ImageView overlayImageView = new ImageView(overlay);

    //RadioButtons
    RadioButton target50 = new RadioButton("target 50");
    RadioButton target100 = new RadioButton("target 100");
    RadioButton target200 = new RadioButton("target 200");
    RadioButton targetEndless = new RadioButton("targetEndless");

    //Database Related
    DatabaseManager database = new DatabaseManager();


    Label scoreLabel = new Label("Score: 0");

    Rectangle rectangle = new Rectangle(150, 80);

    //Score Display HBox
    HBox centerScore = new HBox();

    @Override
    public void start(Stage stage) throws IOException {
        BorderPane borderPane = new BorderPane();
        StackPane stackPane = new StackPane();
        Scene scene = new Scene(borderPane);
        BorderPane settingBorderPane = new BorderPane();
        Scene settingScene = new Scene(settingBorderPane);
        //settingBorderPane.setStyle("-fx-background-color: #fede99;");

        //Check if connect to database, get high score from database
        if(connectionState)
        {
            JDBCMethod.getHighScoreDB();
            //Ask user to sync data if the high score is different between database and local.
            FileHandling.SyncData();
        } else {
            //Get high score from local save.txt
            FileHandling.getHighScoreLocal();
        }

        //Set the stage as not resizable
        stage.setResizable(false);

        //random generate number insert to innerArray
        randomStart();

        //Turn the innerArray into "imageview"
        imageSet();

        //Initialize the start time
        startTime = System.currentTimeMillis();

        VBox overlayVBox = new VBox(overlayImageView);

        //Insert the treeVBox, title and score to the stackpane
        stackPane.getChildren().addAll(treeVBox(),centerScore(),overlayVBox);

        //Set menuBar at the top
        borderPane.setTop(menuBar());
        borderPane.setCenter(stackPane);

        //Insert settingVBox into settingBorderPane
        settingBorderPane.setCenter(settingVBoxMethod());

        //Load the scene to have event lister when key pressed
        scene.setOnKeyPressed(event -> handleKeyPressed(event));
        scene.setOnKeyReleased(event -> handleKeyReleased(event));

        //Set Radio Button actions when click
        target50.setOnAction(event -> {
            targetScore = 50; // Set the target score
            resetGame(); // Reset the game
        });

        //Set Radio Button actions when click
        target100.setOnAction(event -> {
            targetScore = 100; // Set the target score
            resetGame(); // Reset the game
        });

        //Set Radio Button actions when click
        target200.setOnAction(event -> {
            targetScore = 200; // Set the target score
            resetGame(); // Reset the game
        });

        //Set Radio Button actions when click
        targetEndless.setOnAction(event -> {
            targetScore = Integer.MAX_VALUE; // Set a very high target score for endless mode
            resetGame(); // Reset the game
        });

        //Set the scene tile
        stage.setTitle("TimberMan");
        stage.setScene(scene);
        stage.show();

        //Button Event
        settingMenuItem.setOnAction(event -> {
            stage.setTitle("Setting");
            stage.setScene(settingScene);
            stage.show();
        });

        backToGameButton.setOnAction(event -> {
            stage.setTitle("TimberMan");
            stage.setScene(scene);
            stage.show();
        });

        deleteButton.setOnAction(event ->{
            int choice = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure to reset data?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                System.out.println("User chose YES to reset");
                if (connectionState)
                {
                    JDBCMethod.resetRecordDB(); //Reset DB if connect to database

                } else {
                    FileHandling.resetRecordLocal(); //Reset Local Variable and save.txt
                }
            } else if (choice == JOptionPane.NO_OPTION) {
                System.out.println("User chose NO to reset.");
            } else {
                System.out.println("User closed the reset dialog.");
            }



        });
        //Button Event End

        //Help Alert Box
        helpMenuItem.setOnAction(e->{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Help");
            alert.setContentText("1. Chop the tree by using left and right arrow."
                    +"\n" + "2. Default target is 50, target can be change in setting on the top left corner."
            +"\n" + "3. Be careful by not being hit by he branch."
            + "\n" + "4. Good luck have fun.");
            alert.showAndWait();
        });

    }

    public static void main(String[] args) {
        launch();
    }

    private void handleKeyPressed(KeyEvent event) {
        //Get keyPressed
        String keyPressed = event.getCode().toString();

        //Set startTime and turn gameStared to true if the gameStarted = false
        if (keyPressed == "LEFT") {
            playerImageView.setImage(chopLeftImage); //When press change image in the playerImageView
            if (!gameStarted) {
                startTime = System.currentTimeMillis();
                overlayImageView.setVisible(false);
                gameStarted = true;
            }

        } else if (keyPressed == "RIGHT") {
            playerImageView.setImage(chopRightImage); //When press change image in the playerImageView
            if (!gameStarted) {
                startTime = System.currentTimeMillis();
                overlayImageView.setVisible(false);
                gameStarted = true;
            }
        }

    }

    private void handleKeyReleased(KeyEvent event) {
        String keyPressed = event.getCode().toString();
        if (keyPressed == "LEFT") {
            //Validate the score
            if (currentScore >= targetScore-1) {
                long endTime = System.currentTimeMillis();
                //Calculate the time used to complete the game
                String timeString = calculateElapsedTime(startTime, endTime);

                //Set and get highscore depend on the connectionState
                if (connectionState)
                {
                    JDBCMethod.setHighScoreDB(resultTime);
                    JDBCMethod.getHighScoreDB();
                } else if (!connectionState) {
                    FileHandling.setHighScoreLocal(resultTime);
                }

                //Alert Box(Complete)
                Alert congratsAlert = new Alert(Alert.AlertType.INFORMATION);
                congratsAlert.setTitle("Congratulations!");
                congratsAlert.setContentText("You reached the target score of " + targetScore + "!\nTime: " + timeString +"\n\n"+ "High Score"+"\n"+"Target 50: " + hTime50+" seconds"
                        +"\nTarget 100: " + hTime100+" seconds"
                        +"\nTarget 200: " + hTime200+" seconds"
                        +"\nEndless: " + hScoreEndless+" score");
                congratsAlert.showAndWait();

                //Reset the game and variable
                resetGame();
            } else {
                if (innerArray[0] == 1) {
                    randomArrayAction(); //Queue, Push
                    imageSet();
                    //Show the player dead
                    playerImageView.setImage(deadLeftImage);
                    long endTime = System.currentTimeMillis();
                    //Calculate the time used to complete the game
                    String timeString = calculateElapsedTime(startTime, endTime);
                    //Set and get highscore depend on the connectionState
                    if (targetScore != 50 && targetScore != 100 && targetScore != 200) {
                        if (connectionState == true)
                        {
                            JDBCMethod.setHighScoreDB(resultTime);
                            JDBCMethod.getHighScoreDB();
                        } else if (connectionState == false) {
                            FileHandling.setHighScoreLocal(resultTime);
                        }
                    }
                    //Alert Box (Lose)
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("You Lose!");
                    alert.setContentText("Score: " + currentScore + "\nTime: " + timeString +"\n\n"+ "High Score"+"\n"+"Target 50: " + hTime50+" seconds"
                            +"\nTarget 100: " + hTime100+" seconds"
                            +"\nTarget 200: " + hTime200+" seconds"
                            +"\nEndless: " + hScoreEndless+" score");
                    alert.showAndWait();
                    //Reset the game and variable
                    resetGame();
                } else {
                    playerImageView.setImage(standRightImage); //set the image of playerImageView
                    randomArrayAction(); //Queue, Push
                    imageSet();
                    currentScore++; //+score
                    scoreLabel.setText(Integer.toString(currentScore)); // Update the scoreLabel
                }
            }
        } else if (keyPressed == "RIGHT") {
            if (currentScore >= targetScore-1) {
                long endTime = System.currentTimeMillis();
                //Calculate the time used to complete the game
                String timeString = calculateElapsedTime(startTime, endTime);
                //Set and get highscore depend on the connectionState
                if (connectionState)
                {
                    JDBCMethod.setHighScoreDB(resultTime);
                    JDBCMethod.getHighScoreDB();
                } else if (!connectionState) {
                    FileHandling.setHighScoreLocal(resultTime);
                }

                //Alert Box
                Alert congratsAlert = new Alert(Alert.AlertType.INFORMATION);
                congratsAlert.setTitle("Congratulations!");
                congratsAlert.setContentText("You reached the target score of " + targetScore + "!\nTime: " + timeString +"\n\n"+ "High Score"+"\n"+"Target 50: " + hTime50+" seconds"
                        +"\nTarget 100: " + hTime100+" seconds"
                        +"\nTarget 200: " + hTime200+" seconds"
                        +"\nEndless: " + hScoreEndless+" score");
                congratsAlert.showAndWait();

                //Reset the game and variable
                resetGame();
            } else {
                if (innerArray[0] == 2) {
                    randomArrayAction();
                    playerImageView.setImage(deadRightImage);
                    long endTime = System.currentTimeMillis();

                    //Calculate the time used to complete the game
                    String timeString = calculateElapsedTime(startTime, endTime);

                    //Set and get highscore depend on the connectionState
                    if (targetScore != 50 && targetScore != 100 && targetScore != 200) {
                        if (connectionState)
                        {
                            JDBCMethod.setHighScoreDB(resultTime);
                            JDBCMethod.getHighScoreDB();
                        } else if (!connectionState) {
                            FileHandling.setHighScoreLocal(resultTime);
                        }
                    }
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("You Lose!");
                    alert.setContentText("Score: " + currentScore + "\nTime: " + timeString +"\n\n"+ "High Score"+"\n"+"Target 50: " + hTime50+" seconds"
                            +"\nTarget 100: " + hTime100+" seconds"
                            +"\nTarget 200: " + hTime200+" seconds"
                            +"\nEndless: " + hScoreEndless+" score");
                    alert.showAndWait();
                    //Reset the game and variable
                    resetGame();
                }else {
                    playerImageView.setImage(standLeftImage);
                    randomArrayAction();
                    imageSet();
                    currentScore++;
                    scoreLabel.setText(Integer.toString(currentScore)); // Update the scoreLabel
                }

            }
        }
    }


    public void randomStart() {
        int[] randomArray = new int[7]; // Create an array with 7 elements

        try {
            Random random = new Random();

            for (int i = 0; i < randomArray.length; i++) {
                // Generate random number between 0, 1 and 2 (inclusive)
                innerArray[i] = random.nextInt(3);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace for debugging purposes

            // Display an alert to inform the user about the issue
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("An Unexpected Error Occurred");
            errorAlert.setContentText("An unexpected error occurred. Please try again later.");
            errorAlert.showAndWait();
        }
    }

    public VBox treeVBox() {
        playerImageView.setImage(standLeftImage);
        VBox treeVBox = new VBox(index6ImageView, index5ImageView, index4ImageView, index3ImageView, index2ImageView, index1ImageView, index0ImageView, playerImageView);
        return treeVBox;
    }
    
    public void imageSet() { //1 = left, 2 = right
        if (innerArray[0] == 1) {
            index0ImageView.setImage(leftTreeImage);
        } else if (innerArray[0] == 2) {
            index0ImageView.setImage(rightTreeImage);
        } else index0ImageView.setImage(noTreeImage);

        if (innerArray[1] == 1) {
            index1ImageView.setImage(leftTreeImage);
        } else if (innerArray[1] == 2) {
            index1ImageView.setImage(rightTreeImage);
        } else index1ImageView.setImage(noTreeImage);

        if (innerArray[2] == 1) {
            index2ImageView.setImage(leftTreeImage);
        } else if (innerArray[2] == 2) {
            index2ImageView.setImage(rightTreeImage);
        } else index2ImageView.setImage(noTreeImage);

        if (innerArray[3] == 1) {
            index3ImageView.setImage(leftTreeImage);
        } else if (innerArray[3] == 2) {
            index3ImageView.setImage(rightTreeImage);
        } else index3ImageView.setImage(noTreeImage);

        if (innerArray[4] == 1) {
            index4ImageView.setImage(leftTreeImage);
        } else if (innerArray[4] == 2) {
            index4ImageView.setImage(rightTreeImage);
        } else index4ImageView.setImage(noTreeImage);

        if (innerArray[5] == 1) {
            index5ImageView.setImage(leftTreeImage);
        } else if (innerArray[5] == 2) {
            index5ImageView.setImage(rightTreeImage);
        } else index5ImageView.setImage(noTreeImage);

        if (innerArray[6] == 1) {
            index6ImageView.setImage(leftTreeImage);
        } else if (innerArray[6] == 2) {
            index6ImageView.setImage(rightTreeImage);
        } else index6ImageView.setImage(noTreeImage);

    }


    private void randomArrayAction() { //Queue,Push
        for (int i = 1; i < innerArray.length; i++) {
            innerArray[i - 1] = innerArray[i];
        }

        Random random = new Random();

        innerArray[6] = random.nextInt(3);

    }

    public VBox settingVBoxMethod(){
        ToggleGroup targetToggleGroup = new ToggleGroup();
        target50.setToggleGroup(targetToggleGroup);
        target100.setToggleGroup(targetToggleGroup);
        target200.setToggleGroup(targetToggleGroup);
        targetEndless.setToggleGroup(targetToggleGroup);

        target50.setSelected(true);
        target50.setPadding(new Insets(10));
        target100.setPadding(new Insets(10));
        target200.setPadding(new Insets(10));
        targetEndless.setPadding(new Insets(10));
        backToGameButton.setPadding(new Insets(10));
        deleteButton.setPadding(new Insets(10));
        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");

        VBox settingVBox = new VBox(backToGameButton, target50,target100,target200,targetEndless,deleteButton);

        return settingVBox;
    }

    private void resetGame() {
        try {
            gameStarted = false; // Reset gameStarted to false
            currentScore = 0; // Reset the current score
            randomStart(); // Reset the game state
            imageSet(); // Update the images
            playerImageView.setImage(standRightImage); // Set the player image
            scoreLabel.setText(Integer.toString(currentScore)); // Update the scoreLabel
            overlayImageView.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace for debugging purposes

            // Display an alert to inform the user about the issue
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("An Unexpected Error Occurred");
            errorAlert.setContentText("An unexpected error occurred. Please try again later.");
            errorAlert.showAndWait();
        }
    }


    //Calculate the time
    private String calculateElapsedTime(long startTime, long endTime) {
        long elapsedTimeInMillis = endTime - startTime;
        long elapsedTimeInSeconds = elapsedTimeInMillis / 1000;
        long minutes = elapsedTimeInSeconds / 60;
        long seconds = elapsedTimeInSeconds % 60;

        String timeString = "";
        if (minutes > 0) {
            seconds += 60;

        }
        timeString += seconds + " seconds";
        resultTime = (int)seconds;

        return timeString;
    }

    //centerScore HBox
    public HBox centerScore(){
        // Set the score label text
        scoreLabel.setText(Integer.toString(currentScore));
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setFont(Font.font("Arial",FontWeight.BOLD,60));


        centerScore.setAlignment(Pos.TOP_CENTER);
        centerScore.setPadding(new Insets(20));

        centerScore.getChildren().add(scoreLabel);

        return centerScore;
    }

    //MenuBar
    public MenuBar menuBar()
    {
        Image iconImage1 = new Image(main.class.getResource("help.png").toString());
        ImageView iconImageView1 = new ImageView(iconImage1);
        iconImageView1.setFitWidth(16);
        iconImageView1.setFitHeight(16);

        Image iconImage2 = new Image(main.class.getResource("tools.png").toString());
        ImageView iconImageView2 = new ImageView(iconImage2);
        iconImageView2.setFitWidth(16);
        iconImageView2.setFitHeight(16);

        helpMenu.getItems().addAll(helpMenuItem);
        helpMenuItem.setGraphic(iconImageView1);

        settingMenu.getItems().addAll(settingMenuItem);
        settingMenuItem.setGraphic(iconImageView2);

        menuBarTop.getMenus().addAll(settingMenu,helpMenu);
        return menuBarTop;
    }

}