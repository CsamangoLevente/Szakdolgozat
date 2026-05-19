package hu.ArcadeFx.Controllers.User;

import hu.ArcadeFx.Models.Model;
import hu.ArcadeFx.Views.UserMenuOption;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserController implements Initializable{
    public BorderPane user_parent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Model.getInstance().getView().getUserSelectedMenuItem().addListener(((observable, oldValue, newValue) -> {
            if (Objects.requireNonNull(newValue) == UserMenuOption.LOGOUT) {
                Stage stage = (Stage) user_parent.getScene().getWindow();
                Model.getInstance().getView().logout(stage);
            } else {
                user_parent.setCenter(Model.getInstance().getView().getGamesView());
            }
        }));
    }
}
