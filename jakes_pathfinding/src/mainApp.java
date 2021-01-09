import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.HPos;
import javafx.scene.*;
import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.canvas.*;
import javafx.stage.Stage;
import javafx.scene.paint.*;
import javafx.scene.paint.Color.*;
import java.util.Arrays;
import java.util.List;


public class mainApp extends Application implements EventHandler<ActionEvent> {

    private Stage window;
    private Button button;
    private maze Maze;
    private GridPane layout;
//    private GraphicsContext gc;
    private static Canvas canvas;
    private int sHeight = 1000;
    private int sWidth = 1000;
    final private Color _DARKGREY = Color.rgb(50,50,50);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println();
        window = primaryStage;
        window.setTitle("Maze Generation");

        //Layout
        layout = new GridPane();
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.setVgap(20);
        layout.setHgap(20);


        //Maze Object
        Maze = new maze(30,30);
        Maze.generateMaze();

        //Button
        button = new Button();
        button.setText("Generate new maze");
        button.setStyle("-fx-font-size: 2em;-fx-background-radius: 8,7,6;");
        button.setOnAction(e -> Maze.generateMaze());
        GridPane.setConstraints(button, 0, 1);
        GridPane.setHalignment(button, HPos.CENTER);

        //Canvas
        double minSquare = 0.8 * Math.min(sHeight, sWidth);
        canvas = new Canvas(minSquare, minSquare);
        GridPane.setConstraints(canvas, 0, 0);
        GridPane.setHalignment(canvas, HPos.CENTER);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Maze.drawMaze(gc);



        layout.getChildren().addAll(canvas, button);
        Group root = new Group(layout);
        Scene scene = new Scene(root, sWidth, sHeight, _DARKGREY);
        window.setScene(scene);
        window.show();


        new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, window.getWidth(), window.getHeight());
                Maze.drawMaze(gc);
                layout.setPadding(new Insets(20, 20, (window.getWidth() - canvas.getWidth())/2, (window.getWidth() - canvas.getWidth())/2));

            }
        }.start();
    }


    @Override
    public void handle(ActionEvent event) {
//        if (event.getSource() == button){
//            System.out.println("pressed");
//        }
    }



}
/* OUTTAKES

        //Name Label
        Label nameLabel = new Label("Username:");
        GridPane.setConstraints(nameLabel, 0, 0);

        //Name Input
        TextField nameInput = new TextField();
        nameInput.setPromptText("Name Input");
        GridPane.setConstraints(nameInput, 1, 0);

*/