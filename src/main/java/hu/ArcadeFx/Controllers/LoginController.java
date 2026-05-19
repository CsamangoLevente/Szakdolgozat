package hu.ArcadeFx.Controllers;

import hu.ArcadeFx.Models.Model;
import hu.ArcadeFx.Views.AccountType;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public ChoiceBox<AccountType> account_selector;
    public Label user_address_label;
    public TextField user_address_field;
    public TextField password_field;
    public Button login_button;
    public Label error_label;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        account_selector.setItems(FXCollections.observableArrayList(AccountType.ADMIN, AccountType.GAMER));
        account_selector.setValue(Model.getInstance().getView().getLoginAccountType());
        account_selector.valueProperty().addListener(observable -> Model.getInstance().getView().setLoginAccountType(account_selector.getValue()));
        login_button.setOnAction(event -> onLogin());
    }

    private void onLogin() {
        Stage stage = (Stage) error_label.getScene().getWindow();
        if (Model.getInstance().getView().getLoginAccountType() == AccountType.GAMER) {
            Model.getInstance().evaluateUserCred(user_address_field.getText(), password_field.getText());
            if (Model.getInstance().getUserLoginSuccess()) {
                Model.getInstance().getView().showUserWindow();
                Model.getInstance().getView().closeStage(stage);
            } else {
                user_address_field.setText("");
                password_field.setText("");
                error_label.setText("Nem megfelelő felhasználónév és jelszó!");
            }
        } else {
            Model.getInstance().evaluateUserCred(user_address_field.getText(), password_field.getText());
            if (Model.getInstance().getUserLoginSuccess()) {
                Model.getInstance().getView().showAdminWindow();
                Model.getInstance().getView().closeStage(stage);
            } else {
                user_address_field.setText("");
                password_field.setText("");
                error_label.setText("Nem megfelelő felhasználónév és jelszó!");
            }
        }
    }
}
