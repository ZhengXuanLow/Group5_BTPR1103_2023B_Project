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
import java.io.IOException;
import java.util.Random;


public class main extends Application {

    Integer[] innerArray = new Integer[7];
    protected int resultTime = 0;
    protected static int hTime50 = 50;
    protected static int hTime100 = 100;
    protected static int hTime200 = 200;
    protected static int hScoreEndless = 0;
    protected static int currentScore = 0;
    protected static int targetScore = 50;
    protected static boolean connectionState = false;
    boolean gameState = false;
    long startTime;
    Menu settingMenu = new Menu("Setting");
    Menu helpMenu = new Menu("Help");
    MenuItem settingMenuItem = new MenuItem("Setting");
    MenuBar menuBarTop = new MenuBar();
    Button startGameButton = new Button("Start");
    Button backToGameButton = new Button("Back");
    Button deleteButton = new Button("Reset Record");
    MenuItem helpMenuItem = new MenuItem("Help");
    Label titleLabel = new Label("TimberMan " + "\n     Game");
    Image leftTreeImage = new Image(main.class.getResource("left.png").toString());
    Image rightTreeImage = new Image(main.class.getResource("right.png").toString());
    Image noTreeImage = new Image(main.class.getResource("no.png").toString());
    Image standLeftImage = new Image(main.class.getResource("standright.png").toString());
    Image standRightImage = new Image(main.class.getResource("standleft.png").toString());
    Image chopLeftImage = new Image(main.class.getResource("chopleft.png").toString());
    Image chopRightImage = new Image(main.class.getResource("chopright.png").toString());
    Image deadLeftImage = new Image(main.class.getResource("deadleft.png").toString());
    Image deadRightImage = new Image(main.class.getResource("deadright.png").toString());

    RadioButton target50 = new RadioButton("target 50");
    RadioButton target100 = new RadioButton("target 100");
    RadioButton target200 = new RadioButton("target 200");
    RadioButton targetEndless = new RadioButton("targetEndless");

    ImageView playerImageView = new ImageView();
    ImageView index0ImageView = new ImageView();
    ImageView index1ImageView = new ImageView();
    ImageView index2ImageView = new ImageView();
    ImageView index3ImageView = new ImageView();
    ImageView index4ImageView = new ImageView();
    ImageView index5ImageView = new ImageView();
    ImageView index6ImageView = new ImageView();
    DatabaseManager database = new DatabaseManager();
    boolean gameStarted = false; // Add this variable to track if the game has started
    Label scoreLabel = new Label("Score: 0");

    Rectangle rectangle = new Rectangle(150, 80);
    HBox centerScore = new HBox();

    @Override
    public void start(Stage stage) throws IOException {
        BorderPane borderPane = new BorderPane();
        StackPane stackPane = new StackPane();
        Scene scene = new Scene(borderPane);
        BorderPane settingBorderPane = new BorderPane();
        Scene settingScene = new Scene(settingBorderPane);

        if(connectionState == true)
        {
            JDBCMethod.getHighScoreDB();
            FileHandling.SyncData();
        } else {
            FileHandling.getHighScoreLocal();
        }

        stage.setResizable(false);

        randomStart();
        imageSet();
        startTime = System.currentTimeMillis();// Initialize the start time

        stackPane.getChildren().addAll(treeVBox(),centerTitle(),centerScore());
        borderPane.setTop(menuBar());
        borderPane.setCenter(stackPane);
        settingBorderPane.setCenter(settingVBoxMethod());


        scene.setOnKeyPressed(event -> handleKeyPressed(event));
        scene.setOnKeyReleased(event -> handleKeyReleased(event));

        target50.setOnAction(event -> {
            targetScore = 50; // Set the target score
            resetGame(); // Reset the game
        });

        target100.setOnAction(event -> {
            targetScore = 100; // Set the target score
            resetGame(); // Reset the game
        });

        target200.setOnAction(event -> {
            targetScore = 200; // Set the target score
            resetGame(); // Reset the game
        });

        targetEndless.setOnAction(event -> {
            targetScore = Integer.MAX_VALUE; // Set a very high target score for endless mode
            resetGame(); // Reset the game
        });

        stage.setTitle("TimberMan");
        stage.setScene(scene);
        stage.show();

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

        startGameButton.setOnAction(event -> {
            stage.setTitle("TimberMan");
            stage.setScene(scene);
            stage.show();
        });

        startGameButton.setOnAction(event -> {
            gameState = true; // Start the game
            stage.setTitle("TimberMan");
            stage.setScene(scene);
            stage.show();

            startGameButton.setVisible(false); // Hide the start button
            titleLabel.setVisible(false); // Hide the title label
            rectangle.setVisible(false);
            centerScore.setVisible(true);
        });

        deleteButton.setOnAction(event ->{
            if (connectionState == true)
            {
                JDBCMethod.resetRecordDB();

            } else {
                FileHandling.resetRecordLocal();
            }

        });

        helpMenuItem.setOnAction(e->{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Help");
            alert.setContentText("1. Chop the tree by using left and right arrow."
            +"\n" + "2. Be careful by not being hit by he branch."
            + "\n" + "3. Good luck have fun.");
            alert.showAndWait();
        });




    }

    public static void main(String[] args) {
        launch();
    }

    private void handleKeyPressed(KeyEvent event) {
        String keyPressed = event.getCode().toString();
        if (!gameStarted) {
            startTime = System.currentTimeMillis();
            gameStarted = true;
        }
        if (keyPressed == "LEFT") {
            playerImageView.setImage(chopLeftImage);

        } else if (keyPressed == "RIGHT") {
            playerImageView.setImage(chopRightImage);
        }

    }

    private void handleKeyReleased(KeyEvent event) {
        String keyPressed = event.getCode().toString();
        if (keyPressed == "LEFT") {
            if (currentScore >= targetScore-1) {
                long endTime = System.currentTimeMillis();
                String timeString = calculateElapsedTime(startTime, endTime);
                if (connectionState == true)
                {
                    JDBCMethod.setHighScoreDB(resultTime);
                    JDBCMethod.getHighScoreDB();
                } else if (connectionState == false) {
                    FileHandling.setHighScoreLocal(resultTime);
                }

                Alert congratsAlert = new Alert(Alert.AlertType.INFORMATION);
                congratsAlert.setTitle("Congratulations!");
                congratsAlert.setContentText("You reached the target score of " + targetScore + "!\nTime: " + timeString +"\n\n"+ "High Score"+"\n"+"Target 50: " + hTime50+" seconds"
                        +"\nTarget 100: " + hTime100+" seconds"
                        +"\nTarget 200: " + hTime200+" seconds"
                        +"\nEndless: " + hScoreEndless+" score");
                congratsAlert.showAndWait();
                resetGame();
            } else {
                if (innerArray[0] == 1) {
                    randomArrayAction();
                    imageSet();
                    playerImageView.setImage(deadLeftImage);
                    long endTime = System.currentTimeMillis();
                    String timeString = calculateElapsedTime(startTime, endTime);
                    if (targetScore != 50 && targetScore != 100 && targetScore != 200) {
                        if (connectionState == true)
                        {
                            JDBCMethod.setHighScoreDB(resultTime);
                            JDBCMethod.getHighScoreDB();
                        } else if (connectionState == false) {
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
                    resetGame();
                } else {
                    playerImageView.setImage(standRightImage);
                    randomArrayAction();
                    imageSet();
                    currentScore++;
                    scoreLabel.setText(Integer.toString(currentScore)); // Update the scoreLabel
                }
            }
        } else if (keyPressed == "RIGHT") {
            if (currentScore >= targetScore-1) {
                long endTime = System.currentTimeMillis();
                String timeString = calculateElapsedTime(startTime, endTime);
                if (connectionState == true)
                {
                    JDBCMethod.setHighScoreDB(resultTime);
                    JDBCMethod.getHighScoreDB();
                } else if (connectionState == false) {
                    FileHandling.setHighScoreLocal(resultTime);
                }
                Alert congratsAlert = new Alert(Alert.AlertType.INFORMATION);
                congratsAlert.setTitle("Congratulations!");
                congratsAlert.setContentText("You reached the target score of " + targetScore + "!\nTime: " + timeString +"\n\n"+ "High Score"+"\n"+"Target 50: " + hTime50+" seconds"
                        +"\nTarget 100: " + hTime100+" seconds"
                        +"\nTarget 200: " + hTime200+" seconds"
                        +"\nEndless: " + hScoreEndless+" score");
                congratsAlert.showAndWait();
                resetGame();
            } else {
                if (innerArray[0] == 2) {
                    randomArrayAction();
                    playerImageView.setImage(deadRightImage);
                    long endTime = System.currentTimeMillis();
                    String timeString = calculateElapsedTime(startTime, endTime);


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
        } catch (Exception e) { //这里！！//这里！！//这里！！//这里！！
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
    
    public void imageSet() {
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


    private void randomArrayAction() {
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

        VBox settingVBox = new VBox(backToGameButton, target50,target100,target200,targetEndless,deleteButton);

        return settingVBox;
    }

    private void resetGame() {
        try {
            gameStarted = false; // Reset gameStarted to false    //这里！！//这里！！//这里！！
            currentScore = 0; // Reset the current score
            randomStart(); // Reset the game state
            imageSet(); // Update the images
            playerImageView.setImage(standRightImage); // Set the player image
            scoreLabel.setText(Integer.toString(currentScore)); // Update the scoreLabel
        } catch (Exception e) {  //这里！！//这里！！//这里！！//这里！！
            e.printStackTrace(); // Print the stack trace for debugging purposes

            // Display an alert to inform the user about the issue
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("An Unexpected Error Occurred");
            errorAlert.setContentText("An unexpected error occurred. Please try again later.");
            errorAlert.showAndWait();
        }
    }

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

    public VBox centerTitle(){

        VBox centerTitle = new VBox();
        centerTitle.setAlignment(Pos.CENTER);
        centerTitle.setPadding(new Insets(10));

        return centerTitle;
    }

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