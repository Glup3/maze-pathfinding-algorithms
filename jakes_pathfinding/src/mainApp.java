import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.canvas.*;
import javafx.stage.Stage;
import javafx.scene.paint.*;


public class mainApp extends Application implements EventHandler<ActionEvent> {

    private Stage window;
    private maze Maze;
    private GridPane layout;
    private static Canvas canvas;
    public static String informationText = "Waiting for Input";
    final private Color _DARKGREY = Color.rgb(50, 50, 50);
    //    private GraphicsContext gc;
//        private Button recursiveButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        final int INITIAL_SCENE_WIDTH = 1000;
        final int INITIAL_SCENE_HEIGHT = 1000;

        window = primaryStage;
        window.setTitle("Maze Generation");

        //******** Layout ********
        layout = new GridPane();
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.setVgap(20);
        layout.setHgap(20);
        //Hbox for buttons
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(20);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setConstraints(buttonBox, 0, 1);

        //******** Maze Object ********
        Maze = new maze(20, 20);
        Maze.generateMaze(maze.BLANK);

        //******** Buttons ********
        Button recursiveButton = new Button();
        recursiveButton.setText("Generate Recursively");
        recursiveButton.setStyle("-fx-font-size: 2em;-fx-background-radius: 8,7,6;");
        recursiveButton.setOnAction(e -> Maze.generateMaze(maze.DFS_REC));
//        GridPane.setConstraints(recursiveButton, 0, 1);
//        GridPane.setHalignment(recursiveButton, HPos.CENTER);
        //-  -  -  -  -  -  -  -  -  -  -  -  -
        Button iterativeButton = new Button();
        iterativeButton.setText("Generate  Iteratively");
        iterativeButton.setStyle("-fx-font-size: 2em;-fx-background-radius: 8,7,6;");
        iterativeButton.setOnAction(e -> Maze.generateMaze(maze.DFS_ITER));

        //******** Info Text Label ********
        Label infoTextLabel = new Label();
        infoTextLabel.setStyle("-fx-font-size: 2em;-fx-background-radius: 8,7,6;-fx-text-fill: white;");
        GridPane.setConstraints(infoTextLabel, 0, 2);


        //******** Canvas ********
        double minSquare = 0.8 * Math.min(INITIAL_SCENE_HEIGHT, INITIAL_SCENE_WIDTH);
        canvas = new Canvas(minSquare, minSquare);
        GridPane.setConstraints(canvas, 0, 0);
        GridPane.setHalignment(canvas, HPos.CENTER);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Maze.drawMaze(gc);

        //******** Scene and Stage ********
        buttonBox.getChildren().addAll(recursiveButton, iterativeButton);
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
                layout.setPadding(new Insets(20, 20, (window.getWidth() - canvas.getWidth()) / 2, (window.getWidth() - canvas.getWidth()) / 2));
                Maze.iterativeGeneration();
                Maze.drawMaze(gc);
            }
        }.start();
    }



    @Override
    public void handle(ActionEvent event) {
//        if (event.getSource() == recursiveButton){
//            System.out.println("pressed");
//        } else{
//            System.out.println("something else happened");
//        }
    }


}
