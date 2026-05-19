package hu.ArcadeFx.Controllers.User;

import hu.ArcadeFx.Models.Model;
import hu.ArcadeFx.Views.AdminMenuOption;
import hu.ArcadeFx.Views.UserMenuOption;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class UserMenuController implements Initializable {
    public Button games_button;
    public Button logout_button;
    public Button report_button;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListeners();
    }

    private void addListeners() {
        games_button.setOnAction(event -> onGames());
        logout_button.setOnAction(event -> onLogout());
    }

    private void onGames() {
        Model.getInstance().getView().getUserSelectedMenuItem().set(UserMenuOption.GAMES);
    }

    private void onLogout() {
        Model.getInstance().getView().getUserSelectedMenuItem().set(UserMenuOption.LOGOUT);
    }
}
