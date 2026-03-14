package application;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javafx.application.Platform;

public class GlobalKeyHandler implements NativeKeyListener {

    private Controller controller;

    // This is the "bridge" that connects the listener to your Controller file
    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void startListening() {
        try {
            // Check if already registered to avoid errors
            if (!GlobalScreen.isNativeHookRegistered()) {
                GlobalScreen.registerNativeHook();
                GlobalScreen.addNativeKeyListener(this);
                System.out.println("Listener is now LIVE.");
            }
        } catch (NativeHookException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_F9) {
            // Jump from the "Native Thread" to the "JavaFX Thread"
            Platform.runLater(() -> {
                if (controller != null) {
                    // Logic: If already paused, resume. If not, pause.
                    if (controller.pause) {
                        try {
                            controller.play();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        controller.pause();
                    }
                }
            });
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {}

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {}
}
