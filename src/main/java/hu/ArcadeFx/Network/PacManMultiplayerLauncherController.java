package hu.ArcadeFx.Network;

import javafx.animation.PauseTransition;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PacManMultiplayerLauncherController {
    public CheckBox hostCheckBox;
    public TextField ipField;
    public Button startButton;
    public Label infoLabel;

    public void initialize() {
        startButton.setOnAction(event -> startMultiplayer());
    }

    private void startMultiplayer() {
        String ip = ipField.getText().trim();
        if (ip.isEmpty()) {
            infoLabel.setText("Adj meg egy IP címet!");
            return;
        }
        Stage launcherStage = (Stage) startButton.getScene().getWindow();
        if (hostCheckBox.isSelected()) {
            infoLabel.setText("Szerver indítása...");
            Thread serverThread = new Thread(() -> PacManServer.main(new String[]{}));
            serverThread.setDaemon(true);
            serverThread.start();
            PauseTransition pause = new PauseTransition(Duration.millis(800));
            pause.setOnFinished(e -> openClient(ip, launcherStage));
            pause.play();
        } else {
            openClient(ip, launcherStage);
        }
    }

    private void openClient(String ip, Stage launcherStage) {
        try {
            PacManClient client = new PacManClient(ip, launcherStage::close);
            Stage clientStage = new Stage();
            client.start(clientStage);
        } catch (Exception e) {
            e.printStackTrace();
            infoLabel.setText("Nem sikerült csatlakozni!");
        }
    }
}