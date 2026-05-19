package hu.ArcadeFx.Controllers.Admin;

import hu.ArcadeFx.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddUserController implements Initializable {
    public TextField last_name_tfield;
    public TextField first_name_tfield;
    public TextField password_tfield;
    public CheckBox user_id_cbox;
    public Label user_id_label;
    public Button add_user_button;
    public Label error_label;

    private String userID;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        add_user_button.setOnAction(event -> createPlayer());
        user_id_cbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                userID = createUserId();
                onCreatePlayerId();
            }
        });

    }

    private void createPlayer() {
        String lastName = last_name_tfield.getText();
        String firstName = first_name_tfield.getText();
        String password = password_tfield.getText();
        boolean created = Model.getInstance().createPlayer(firstName, lastName, userID, password, LocalDate.now());
        if (created) {
            error_label.setStyle("-fx-text-fill: blue; -fx-font-size: 1.3em; -fx-font-weight: bold;");
            error_label.setText("A játékos létre jött!");
            emptyFields();
            userID = null;
        } else {
            error_label.setStyle("-fx-text-fill: red; -fx-font-size: 1.3em; -fx-font-weight: bold;");
            error_label.setText("Hiba történt a játékos létrehozásakor!");
        }
    }

    private void onCreatePlayerId() {
        if (first_name_tfield.getText() != null && last_name_tfield.getText() != null) {
            user_id_label.setText(userID);
        }
    }

    private String createUserId() {
        String firstName = first_name_tfield.getText().trim();
        String lastName = last_name_tfield.getText().trim();
        if (firstName.isEmpty() || lastName.isEmpty()) {
            return "";
        }
        int id = Model.getInstance().getDatabase().getLastPlayerId() + 1;
        char fchar = Character.toLowerCase(firstName.charAt(0));
        return "#" + fchar + lastName + id;
    }

    private void emptyFields() {
        first_name_tfield.setText("");
        last_name_tfield.setText("");
        password_tfield.setText("");
        user_id_cbox.setSelected(false);
        user_id_label.setText("");
    }

}
