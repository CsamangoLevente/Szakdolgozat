package hu.ArcadeFx.Controllers.Admin;

import hu.ArcadeFx.Models.Model;
import hu.ArcadeFx.Models.User;
import hu.ArcadeFx.Views.UserCellView;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class DeletUserController implements Initializable {
    public TextField search_textfield;
    public Button search_button;
    public ListView<User> result_listview;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        search_button.setOnAction(e -> onPlayerSearch());
    }

    private void onPlayerSearch() {
        ObservableList<User> result = Model.getInstance().search(search_textfield.getText());
        result_listview.setItems(result);
        result_listview.setCellFactory(e -> new UserCellView(result_listview.getItems()));
    }
}
