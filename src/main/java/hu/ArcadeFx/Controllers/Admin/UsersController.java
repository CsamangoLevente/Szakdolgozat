package hu.ArcadeFx.Controllers.Admin;

import hu.ArcadeFx.Models.Model;
import hu.ArcadeFx.Models.User;
import hu.ArcadeFx.Views.UserCellView;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class UsersController implements Initializable {
    public ListView<User> users_listview;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDatabase();
        users_listview.setItems(Model.getInstance().getPlayersList());
        users_listview.setCellFactory(e -> new UserCellView(users_listview.getItems()));
    }

    private void initDatabase() {
        if (Model.getInstance().getPlayersList().isEmpty()) {
            Model.getInstance().setPlayers();
        }
    }
}
