package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button; // Correct import
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class Controller2 {

    @FXML
    private Button switchhh; // Ensure this matches the fx:id in your FXML file

    @FXML
    ScrollPane plan;

    @FXML
    Circle circleee;

    @FXML
    Button delete;

    TilePane tilePane;
    Controller crController;
    Parent root;
    Mixer spker;
    boolean micoin;
    Thread audioThread;
    AudioFormat format = new AudioFormat(
        AudioFormat.Encoding.PCM_SIGNED, // Encoding
        44100, // Sample rate (Hz)
        16, // Sample size (bits per sample)
        2, // Channels (stereo)
        4, // Frame size (bytes per frame)
        44100, // Frame rate (frames per second)
        true // Big-endian
    );
    FXMLLoader loader = new FXMLLoader(getClass().getResource("gg.fxml"));

    public void del() {
        Stage stgStage = new Stage();

        stgStage.show();
    }

    public void mic_on() throws LineUnavailableException {
        System.out.println("got it" + micoin + spker);
        if (micoin == true && spker != null) {
            DataLine.Info targetInfo = new DataLine.Info(
                TargetDataLine.class,
                format
            );
            TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(
                targetInfo
            );
            microphone.open(format);

            DataLine.Info sourceInfo = new DataLine.Info(
                SourceDataLine.class,
                format
            );
            SourceDataLine speakers = (SourceDataLine) spker.getLine(
                sourceInfo
            );
            speakers.open(format);

            microphone.start();
            speakers.start();
            System.out.println("Audio loopback started");
            //	        cirlce.setFill(Color.LIGHTGREEN);

            // Buffer for audio data
            //	        byte[] buffer = new byte[1024]; // Adjust buffer size as needed
            //	        int bytesRead;

            // Loopback audio
            Task<Void> audioTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    byte[] buffer = new byte[1024]; // Adjust buffer size as needed
                    int bytesRead;

                    // Loopback audio
                    while (micoin) {
                        // Stop if toggle is switched off
                        bytesRead = microphone.read(buffer, 0, buffer.length);
                        if (bytesRead > 0) {
                            speakers.write(buffer, 0, bytesRead);
                        }
                    }

                    // Clean up resources when the loop stops
                    microphone.stop();
                    microphone.close();
                    speakers.stop();
                    speakers.close();

                    return null;
                }
            };

            // Start the audio processing in a new thread

            audioThread = new Thread(audioTask);
            audioThread.setDaemon(true);
            audioThread.setPriority(Thread.MAX_PRIORITY); // Allows the thread to terminate when the application closes
            audioThread.start();
            circleee.setFill(Color.LIGHTGREEN);
        } else {
            circleee.setFill(Color.RED);
        }
    }

    public void initialize() throws IOException, LineUnavailableException {
        tilePane = new TilePane();
        tilePane.setHgap(10);
        tilePane.setVgap(10);
        tilePane.setPrefColumns(6);
        root = loader.load();
        crController = loader.getController();
        File myObj = new File("gg.txt");
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            System.out.println(data);
            File dataFile = new File(data);
            getofile(dataFile);
        }
        myReader.close();

        plan.setContent(tilePane);
    }

    public void changescene()
        throws IOException, LineUnavailableException, InterruptedException {
        Boolean mjBoolean = micoin;
        micoin = false;
        // Load the second FXML
        //        Parent root = loader.load();

        // Get current stage
        Stage stage = (Stage) plan.getScene().getWindow();

        stage.setScene(new Scene(root));
        crController.setthreadd(mjBoolean, spker, audioThread);
    }

    //    public void getit(int r) {
    //    	int r = r;
    //    }
    public Button maketile(String fileNameWithoutExtension) {
        Button button1 = new Button(fileNameWithoutExtension);
        Tooltip tooltip1 = new Tooltip(fileNameWithoutExtension);
        button1.setTooltip(tooltip1);
        Image image = new Image("file:images.png"); // Assuming the images are in a folder named "icons"
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(60);
        imageView.setPreserveRatio(true);
        button1.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);
        button1.setMaxWidth(60);
        tilePane.getChildren().add(button1);
        button1.setGraphic(imageView);
        return button1;
    }

    public void getofile(File i) {
        String fullFileName = i.getName();
        String fileNameWithoutExtension;
        int lastDotIndex = fullFileName.lastIndexOf('.');

        if (lastDotIndex > 0) {
            // Ensure there is a period (.)
            fileNameWithoutExtension = fullFileName.substring(0, lastDotIndex);
        } else {
            // If no extension, use the full file name
            fileNameWithoutExtension = fullFileName;
        }

        Button button1 = maketile(fileNameWithoutExtension);
        button1.setOnAction(event -> {
            crController.set_path(i.getAbsolutePath());
            //        	.interrupt();
            //        	System.out.println(audioThread.isAlive());

            try {
                changescene();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (LineUnavailableException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }

    public void newfile() throws IOException {
        FileChooser fileChooser = new FileChooser();

        Stage stage = (Stage) plan.getScene().getWindow();
        //        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Sound clip", "*.mp3", "*.wav"));

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);
        FileWriter myWriter = new FileWriter("gg.txt", true);
        if (selectedFiles != null) {
            for (File i : selectedFiles) {
                getofile(i);
                myWriter.write(i.getAbsolutePath() + "\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        }
    }

    public void setthread(Boolean thre, Mixer spk, Thread auThread)
        throws LineUnavailableException, InterruptedException {
        System.out.println(thre);
        micoin = thre;
        spker = spk;
        if (auThread != null) {
            auThread.join();
        }
        mic_on();
    }
}
