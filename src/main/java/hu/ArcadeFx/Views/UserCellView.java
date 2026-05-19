package hu.ArcadeFx.Views;

import hu.ArcadeFx.Controllers.Admin.UserCellController;
import hu.ArcadeFx.Models.User;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;


public class UserCellView extends ListCell<User> {
    private final ObservableList<User> sourceList;

    public UserCellView(ObservableList<User> sourceList) {
        this.sourceList = sourceList;
    }

    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        if (empty || user == null) {
            setText(null);
            setGraphic(null);
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/UserCell.fxml"));
            UserCellController controller = new UserCellController(user, sourceList);
            loader.setController(controller);
            setText(null);
            try {
                setGraphic(loader.load());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
