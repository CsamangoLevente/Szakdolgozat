package hu.ArcadeFx.Controllers.Admin;

import hu.ArcadeFx.Models.Model;
import hu.ArcadeFx.Views.AdminMenuOption;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminMenuController implements Initializable {
    public Button games_button;
    public Button users_button;
    public Button add_user_button;
    public Button logout_button;
    public Button delete_user_button;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListeners();
    }

    private void addListeners(){
        games_button.setOnAction(e -> onGames());
        add_user_button.setOnAction(event -> onAddUser());
        users_button.setOnAction(event -> onUsers());
        delete_user_button.setOnAction(event -> onDeleteUser());
        logout_button.setOnAction(event -> onLogout());
    }

    private void onGames() {
        Model.getInstance().getView().getAdminSelectedMenuItem().set(AdminMenuOption.GAMES);
    }

    private void onAddUser() {
        Model.getInstance().getView().getAdminSelectedMenuItem().set(AdminMenuOption.ADD_USER);
    }

    private void onUsers() {
        Model.getInstance().getView().getAdminSelectedMenuItem().set(AdminMenuOption.USERS);
    }

    private void onDeleteUser() {
        Model.getInstance().getView().getAdminSelectedMenuItem().set(AdminMenuOption.DELETE_USER);
    }

    private void onLogout() {
        Model.getInstance().getView().getAdminSelectedMenuItem().set(AdminMenuOption.LOGOUT);
    }


}
