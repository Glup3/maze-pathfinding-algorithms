package org.bonbo.alpha;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.canvas.*;
import javafx.stage.Stage;
import javafx.scene.paint.*;

/**
 * ALL COORDINATES ARE SAVED AS [y][x] WHERE [0][0] IS THE TOP LEFT CORNER
 **/

public class MainApp implements EventHandler<ActionEvent> {

    private Stage window;
    private MazeClass maze;
    private GridPane layout;
    private static Canvas canvas;
    public static String informationText = "Waiting for Input";
    final private Color _DARKGREY = Color.rgb(50, 50, 50);
    //    private GraphicsContext gc;
//        private Button recursiveButton;
//    private static String buttonStyle;
    final static private int ITERATION_SPEED = 10;       //how many iterations are done per frame

    private static MazeSolve solver = new MazeSolve();

    public MainApp(Stage stage) {
        start(stage);
    }

    public void start(Stage primaryStage) {
        int _d = 1000;  //TEMP for quick changes
        final int INITIAL_SCENE_WIDTH = _d;
        final int INITIAL_SCENE_HEIGHT = _d;
        final int MIN_INITIAL_DIM = Math.min(INITIAL_SCENE_WIDTH, INITIAL_SCENE_HEIGHT);
        final int GENERAL_PADDING = MIN_INITIAL_DIM / 50;
        final String buttonStyle = "-fx-font-size: " + (float) MIN_INITIAL_DIM / 400 + "em;-fx-background-radius: 8,7,6;";

        window = primaryStage;
        window.setTitle("Maze Generation");

        //******** Layout ********
        layout = new GridPane();
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.setVgap(GENERAL_PADDING);
        layout.setHgap(GENERAL_PADDING);
        //Hbox for buttons
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(GENERAL_PADDING);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setConstraints(buttonBox, 0, 1);

        //******** Maze Object ********
        maze = new MazeClass(30, 30, solver);
        maze.generateMaze(MazeClass.BLANK);

        //******** Buttons ********
        Button recursiveButton = new Button();
        recursiveButton.setText("Generate Recursively");
        recursiveButton.setStyle(buttonStyle);
        recursiveButton.setOnAction(e -> maze.generateMaze(MazeClass.DFS_REC));

        //-  -  -  -  -  -  -  -  -  -  -  -  -

        Button iterativeButton = new Button();
        iterativeButton.setText("Generate  Iteratively");
        iterativeButton.setStyle(buttonStyle);
        iterativeButton.setOnAction(e -> maze.generateMaze(MazeClass.DFS_ITER));

        //-  -  -  -  -  -  -  -  -  -  -  -  -

        Button solveButton = new Button();
        solveButton.setText("Solve!");
        solveButton.setStyle(buttonStyle);
        solveButton.setOnAction(e -> solver.solveStatus = solver.STARTING);
        // BUTTON BOX - TO DETERMINE SIZE
        buttonBox.getChildren().addAll(recursiveButton, iterativeButton, solveButton);

        //******** Info Text Label ********
        Label infoTextLabel = new Label();
        infoTextLabel.setStyle(buttonStyle + "-fx-text-fill: white;");
        GridPane.setConstraints(infoTextLabel, 0, 2);


        //******** Canvas ********
        double minSquare = 0.8 * MIN_INITIAL_DIM;
        canvas = new Canvas(minSquare * Math.min(1, (float) maze.width / maze.height), minSquare * Math.min(1, (float) maze.height / maze.width));
        GridPane.setConstraints(canvas, 0, 0);
        GridPane.setHalignment(canvas, HPos.CENTER);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        maze.drawMaze(gc);

        //******** Scene and Stage ********
        layout.getChildren().addAll(canvas, buttonBox, infoTextLabel);
        Group root = new Group(layout);
        Scene scene = new Scene(root, INITIAL_SCENE_WIDTH, INITIAL_SCENE_HEIGHT, _DARKGREY);
        window.setScene(scene);
        window.show();


        new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, window.getWidth(), window.getHeight());
                infoTextLabel.setText(informationText);
                double hPadding = (window.getWidth() - Math.max(canvas.getWidth(), buttonBox.getWidth()))*0.5 - GENERAL_PADDING * 0.5;
                layout.setPadding(new Insets(GENERAL_PADDING, hPadding, GENERAL_PADDING, hPadding));
                for (int i = 0; i < ITERATION_SPEED; i++) {
                    solver.followLeft(maze);
                    maze.iterativeGeneration();
                }
                maze.drawMaze(gc);

            }
        }.start();
    }


    @Override
    public void handle(ActionEvent event) {
//        if (event.getSource() == recursiveButton){
//        } else{
//        }
    }


}
