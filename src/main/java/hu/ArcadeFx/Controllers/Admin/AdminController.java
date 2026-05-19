package hu.ArcadeFx.Controllers.Admin;

import hu.ArcadeFx.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    public BorderPane admin_parent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Model.getInstance().getView().getAdminSelectedMenuItem().addListener(((observable, oldValue, newValue) -> {
            switch (newValue) {
                case USERS -> admin_parent.setCenter(Model.getInstance().getView().getUsersView());
                case DELETE_USER ->  admin_parent.setCenter(Model.getInstance().getView().getDeleteView());
                case ADD_USER ->  admin_parent.setCenter(Model.getInstance().getView().getAddUserView());
                case LOGOUT -> {
                    Stage stage = (Stage) admin_parent.getScene().getWindow();
                    Model.getInstance().getView().logout(stage);
                }
                default -> admin_parent.setCenter(Model.getInstance().getView().getGamesView());
            }
        }));
    }
}
