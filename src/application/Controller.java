package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
//import audopr.Controller2;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javax.sound.sampled.*;

public class Controller {

    @FXML
    ComboBox<String> combog;

    @FXML
    ToggleButton togger;

    @FXML
    private Button switchButton;

    @FXML
    Circle cirlce;

    Mixer spkmicMixer2;
    String pathString;
    Clip audioClip;
    Clip clip;
    Mixer.Info[] mixers;
    Properties properties = new Properties();
    FileInputStream fis;
    boolean micon = false;
    boolean ch = false;
    Thread audioThread;
    boolean pause = false;

    AudioFormat format = new AudioFormat(
        AudioFormat.Encoding.PCM_SIGNED, // Encoding
        44100, // Sample rate (Hz)
        16, // Sample size (bits per sample)
        2, // Channels (stereo)
        4, // Frame size (bytes per frame)
        44100, // Frame rate (frames per second)
        true // Big-endian
    );

    public void changescene()
        throws IOException, LineUnavailableException, InterruptedException {
        // Load the second FXML
        ch = micon;
        micon = false;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("gg2.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) switchButton).getScene().getWindow();

        Controller2 controller2 = loader.getController();
        // Set the new scene

        controller2.setthread(ch, spkmicMixer2, audioThread);
        // send the thread that is running
        System.out.println(controller2);
        Random rand = new Random();

        // Generate random integers in range 0 to 999
        int rand_int1 = rand.nextInt(1000);
        //		controller2.getit(rand_int1);
        stage.setScene(new Scene(root));
        if (clip != null) {
            clip.stop();
            audioClip.stop();
        }
    }

    public void delete()
        throws IOException, LineUnavailableException, InterruptedException {
        LinkedList<String> ghLinkedList = new LinkedList<>();
        File myObj = new File("gg.txt");

        try (Scanner myReader = new Scanner(myObj)) {
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (!data.equals(pathString)) {
                    ghLinkedList.add(data);
                }
            }
        }

        try (
            BufferedWriter writer = new BufferedWriter(new FileWriter(myObj))
        ) {
            for (String line : ghLinkedList) {
                writer.write(line);
                writer.newLine();
            }
        }
        changescene();
    }

    public void initialize()
        throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        System.out.println("sup");
        //		audioStream = AudioSystem.getAudioInputStream(audioFile);
        fis = new FileInputStream("config.properties");
        properties.load(fis);

        int def = Integer.parseInt(properties.getProperty("def"));
        int mic = Integer.parseInt(properties.getProperty("mic"));

        mixers = AudioSystem.getMixerInfo();

        combog.getSelectionModel().select(def + " : " + mixers[def].getName());

        for (int i = 0; i < mixers.length; i++) {
            System.out.println(i + " : " + mixers[i].getName());
            combog.getItems().add(i + " : " + mixers[i].getName());
        }
    }

    public void miconn(Mixer spkmicMixer) throws LineUnavailableException {
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
        SourceDataLine speakers = (SourceDataLine) spkmicMixer.getLine(
            sourceInfo
        );
        speakers.open(format);

        microphone.start();
        speakers.start();
        System.out.println("Audio loopback started");
        cirlce.setFill(Color.LIGHTGREEN);

        // Buffer for audio data
        //    byte[] buffer = new byte[1024]; // Adjust buffer size as needed
        //    int bytesRead;

        // Loopback audio
        Task<Void> audioTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                byte[] buffer = new byte[1024]; // Adjust buffer size as needed
                int bytesRead;

                // Loopback audio
                while (micon) {
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
        micon = true;
    }

    public void setthreadd(Boolean thre, Mixer spmiMixer, Thread audioThreadf)
        throws LineUnavailableException, InterruptedException {
        System.out.println(spmiMixer);
        micon = thre;
        spkmicMixer2 = spmiMixer;
        if (micon == true && spkmicMixer2 != null) {
            System.out.println("waww");
            audioThreadf.join();
            miconn(spkmicMixer2);
        }
    }

    public void togg()
        throws LineUnavailableException, IOException, InterruptedException {
        System.out.println("togged");
        boolean isSelected = togger.isSelected();

        if (isSelected) {
            String value = (String) combog.getValue();
            int val = Integer.parseInt(value.split(":")[0].trim());
            Mixer spkmicMixer = AudioSystem.getMixer(mixers[val]);
            spkmicMixer2 = spkmicMixer;
            properties.setProperty("mic", value.split(":")[0].trim());
            try (
                FileOutputStream fos = new FileOutputStream("config.properties")
            ) {
                properties.store(fos, "Updated def property");
            }

            if (micon == false) {
                miconn(spkmicMixer);
            } else {
                cirlce.setFill(Color.RED);
                micon = false;
            }
        } else {
            cirlce.setFill(Color.RED);
            micon = false;
        }
    }

    public void set_path(String path) {
        pathString = path;
    }

    public int pause() {
        if (clip != null && pause == false) {
            clip.stop();
            audioClip.stop();
            pause = true;
        }
        return 0;
    }

    public int play()
        throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        System.out.println("playing..");
        if (Main.keyHandler != null) {
            Main.keyHandler.setController(this);
            // 2. Start the listener
            Main.keyHandler.startListening();
        }
        if (clip != null && pause == true) {
            clip.start();
            audioClip.start();
            pause = false;
            return 0;
        }
        if (pathString == null) {
            Alert a = new Alert(AlertType.NONE);
            a.setAlertType(AlertType.INFORMATION);
            a.setHeaderText(null);

            a.setContentText("No audio file is selected");
            // show the dialog
            a.show();
            return 0;
        }
        File flFile = new File(pathString);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(flFile);
        AudioFormat format = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(Clip.class, format);
        audioClip = (Clip) AudioSystem.getLine(info);

        audioClip.open(audioStream);

        AudioInputStream newAudioStream = AudioSystem.getAudioInputStream(
            flFile
        );

        String value = (String) combog.getValue();
        int val = Integer.parseInt(value.split(":")[0].trim());
        System.out.println(val);
        Mixer.Info selectedMixerInfo = mixers[val]; // Replace '1' with the desired index
        Mixer mixer = AudioSystem.getMixer(selectedMixerInfo);

        properties.setProperty("def", value.split(":")[0].trim());
        FileOutputStream fos = new FileOutputStream("config.properties");
        properties.store(fos, "Updated def property");

        clip = (Clip) mixer.getLine(
            new DataLine.Info(Clip.class, newAudioStream.getFormat())
        );
        clip.open(newAudioStream);

        audioClip.start();
        clip.start();
        return 0;
    }
}
