package org.bonbo.alpha;

import javafx.animation.AnimationTimer;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.canvas.*;
import javafx.stage.Stage;
import javafx.scene.paint.*;

/**
 * ALL COORDINATES ARE SAVED AS [y][x] WHERE [0][0] IS THE TOP LEFT CORNER
 **/

public class MainApp implements EventHandler<ActionEvent> {

    final private Color _DARKGREY = Color.rgb(50, 50, 50);

    final private int ITERATION_SPEED = 20;       //how many iterations are done per frame TODO: make slider

    private MazeClass maze;

    private Stage window;

    // objects in stage
    private GridPane layout;
    private Canvas canvas;
    private GraphicsContext gc;

    private HBox buttonBox;
    private HBox secondBox;

    private Button recursiveButton;
    private Button iterativeButton;
    private Button solveButton;

    private Label infoTextLabel;
    public ComboBox<String> solveComboBox;

    private String informationText = "Waiting for Input";

    private int INITIAL_SCENE_WIDTH;
    private int INITIAL_SCENE_HEIGHT;
    private int MIN_INITIAL_DIM;
    private int GENERAL_PADDING;

    private String buttonStyle;
    private String smallerButtonStyle;


    public MainApp(Stage stage) {
        start(stage);
    }

    public void start(Stage primaryStage) {

        initDimensions();

        window = primaryStage;
        window.setTitle("Maze Generation");

        defineLayout();

        initHBoxes();

        //******** Maze Object ********
        maze = new MazeClass(50, 50, this);       //TODO make grid size dynamic with slider
        maze.solver = new MazeSolver(maze);
        maze.mazeGenerator = new MazeGenerator(maze);
        maze.mazeGenerator.generateMaze(maze.mazeGenerator.BLANK);


        initButtons();

        initLabels_ComboBox();

        initCanvas();

        initStage();


        new AnimationTimer() {
            @Override
            public void handle(long now) {
//                System.out.print("-");
                gc.clearRect(0, 0, window.getWidth(), window.getHeight());
                infoTextLabel.setText(informationText);
                double hPadding = (window.getWidth() - Math.max(canvas.getWidth(), buttonBox.getWidth())) * 0.5 - GENERAL_PADDING * 0.5;
                layout.setPadding(new Insets(GENERAL_PADDING, hPadding, GENERAL_PADDING, hPadding));
                for (int i = 0; i < ITERATION_SPEED; i++) {
                    maze.solver.continueSolve();
                    maze.mazeGenerator.iterativeGeneration();
                }
                maze.drawMaze(gc);

            }
        }.start();
    }


    private void initHBoxes() {
        //Horizontal box for buttons
        buttonBox = new HBox();
        buttonBox.setSpacing(GENERAL_PADDING);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setConstraints(buttonBox, 0, 1);

        //2nd Horizontal box for others
        secondBox = new HBox();
        secondBox.setSpacing(GENERAL_PADDING);
        secondBox.setAlignment(Pos.CENTER);
        GridPane.setConstraints(secondBox, 0, 2);
    }

    private void initButtons() {
        recursiveButton = new Button();
        recursiveButton.setText("Generate Recursively");
        recursiveButton.setStyle(buttonStyle);
        recursiveButton.setOnAction(e -> maze.mazeGenerator.generateMaze(maze.mazeGenerator.DFS_REC));

        //-  -  -  -  -  -  -  -  -  -  -  -  -

        iterativeButton = new Button();
        iterativeButton.setText("Generate Iteratively");
        iterativeButton.setStyle(buttonStyle);
        iterativeButton.setOnAction(e -> maze.mazeGenerator.generateMaze(maze.mazeGenerator.DFS_ITER));

        //-  -  -  -  -  -  -  -  -  -  -  -  -

        solveButton = new Button();
        solveButton.setText("Solve!");
        solveButton.setStyle(buttonStyle);
        solveButton.setOnAction(e -> maze.solver.startNewSolve());
    }

    private void defineLayout() {
        layout = new GridPane();
        layout.setPadding(new Insets(GENERAL_PADDING, GENERAL_PADDING, GENERAL_PADDING, GENERAL_PADDING));
        layout.setVgap(GENERAL_PADDING);
        layout.setHgap(GENERAL_PADDING);
    }

    private void initLabels_ComboBox() {
        //******** Info Text Label ********
        infoTextLabel = new Label();
        infoTextLabel.setStyle(buttonStyle + "-fx-text-fill: white;");
        infoTextLabel.setAlignment(Pos.CENTER_LEFT);        //useless?
        GridPane.setConstraints(infoTextLabel, 0, 2);


        //******** Solve Selection ********
        solveComboBox = new ComboBox<>(maze.solver.solveOptions);
        solveComboBox.setPromptText("Choose solving Algorithm");
        solveComboBox.setStyle(smallerButtonStyle);
        solveComboBox.setOnAction(e -> maze.solver.setCurrentSolveMethod());
    }

    private void initCanvas() {
        //******** Canvas ********
        double minSquare = 0.8 * MIN_INITIAL_DIM;
        canvas = new Canvas(minSquare * Math.min(1, (float) maze.width / maze.height), minSquare * Math.min(1, (float) maze.height / maze.width));
        GridPane.setConstraints(canvas, 0, 0);
        GridPane.setHalignment(canvas, HPos.CENTER);
        gc = canvas.getGraphicsContext2D();
        maze.drawMaze(gc);
    }

    private void initDimensions() {
        final int dim = 1000;

        INITIAL_SCENE_WIDTH = dim;
        INITIAL_SCENE_HEIGHT = dim;

        MIN_INITIAL_DIM = Math.min(INITIAL_SCENE_WIDTH, INITIAL_SCENE_HEIGHT);      //TODO: make dimensions of everything dynamic - dependant on window size
        GENERAL_PADDING = MIN_INITIAL_DIM / 50;

        buttonStyle = "-fx-font-size: " + (float) MIN_INITIAL_DIM / 400 + "em;-fx-background-radius: 8,7,6;";
        smallerButtonStyle = "-fx-font-size: " + (float) MIN_INITIAL_DIM / 800 + "em;-fx-background-radius: 8,7,6;";
    }


    private void initStage() {
        //******** Scene and Stage ********
        buttonBox.getChildren().addAll(recursiveButton, iterativeButton, solveButton);
        secondBox.getChildren().addAll(infoTextLabel, solveComboBox);
        layout.getChildren().addAll(canvas, buttonBox, secondBox);
        Group root = new Group(layout);
        Scene scene = new Scene(root, INITIAL_SCENE_WIDTH, INITIAL_SCENE_HEIGHT, _DARKGREY);
        window.setScene(scene);
        window.show();
    }

    public void setInformationText(String text) {
        informationText = text;
    }


    @Override
    public void handle(ActionEvent event) {
//        if (event.getSource() == recursiveButton){
//        } else{
//        }
    }


}
