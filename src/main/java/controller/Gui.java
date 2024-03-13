import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Gui extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a button
        Button button = new Button("Click me!");

        // Set action for the button
        button.setOnAction(e -> System.out.println("Button clicked!"));

        // Create a layout pane (StackPane)
        StackPane root = new StackPane();
        root.getChildren().add(button);

        // Create a scene with the layout pane
        Scene scene = new Scene(root, 300, 250);

        // Set the scene in the stage
        primaryStage.setScene(scene);

        // Set the title of the stage
        primaryStage.setTitle("Simple JavaFX GUI");

        // Show the stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}
