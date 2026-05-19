package hu.ArcadeFx.Controllers.Admin;

import hu.ArcadeFx.Models.Model;
import hu.ArcadeFx.Models.User;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class UserCellController implements Initializable {
    public Label lname_label;
    public Label user_id_label;
    public Label date_label;
    public Label fname_label;
    public Button delete_button;

    private final User user;
    private final ObservableList<User> sourceList;

    public UserCellController(User user, ObservableList<User> sourceList) {
        this.user = user;
        this.sourceList = sourceList;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fname_label.textProperty().bind(user.firstnameProperty());
        lname_label.textProperty().bind(user.lastnameProperty());
        user_id_label.textProperty().bind(user.userIdProperty());
        date_label.textProperty().bind(user.dateCreatedProperty().asString());
        delete_button.setOnAction(event -> onDelete());
    }

    private void onDelete() {
        boolean deleted = Model.getInstance().deletePlayer(user);
        if (deleted) {
            sourceList.removeIf(u -> u.userIdProperty().get().equals(user.userIdProperty().get()));
        }
    }

}
