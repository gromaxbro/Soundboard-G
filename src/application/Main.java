package application;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static GlobalKeyHandler keyHandler = new GlobalKeyHandler();

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Use an FXMLLoader instance instead of the static load()
            // This allows us to get the Controller after the file loads
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("gg.fxml")
            );
            Parent root = loader.load();

            // 2. Get the controller instance
            Controller controller = loader.getController();

            // 3. Setup the Global Key Listener
            GlobalKeyHandler keyHandler = new GlobalKeyHandler();
            keyHandler.setController(controller);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Soundboard G-Main");

            // 4. Ensure the app exits fully when the window is closed
            primaryStage.setOnCloseRequest(event -> {
                stopNativeHook();
                Platform.exit();
                System.exit(0);
            });

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopNativeHook() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        stopNativeHook();
    }

    public static void main(String[] args) {
        // 5. Disable JNativeHook logging so it doesn't spam your console
        Logger logger = Logger.getLogger(
            GlobalScreen.class.getPackage().getName()
        );
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        launch(args);
    }
}
