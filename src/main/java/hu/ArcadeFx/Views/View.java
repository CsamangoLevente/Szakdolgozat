package hu.ArcadeFx.Views;

import hu.ArcadeFx.Controllers.Admin.AdminController;
import hu.ArcadeFx.Controllers.User.UserController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class View {
    private final ObjectProperty<AdminMenuOption> adminSelectedMenuItem;
    private final ObjectProperty<UserMenuOption> userSelectedMenuItem;
    private AccountType loginAccountType;
    private AnchorPane gamesView;
    private AnchorPane addUserView;
    private AnchorPane usersView;
    private AnchorPane deleteView;

    public View() {
        this.loginAccountType = AccountType.GAMER;
        this.userSelectedMenuItem = new SimpleObjectProperty<>();
        this.adminSelectedMenuItem = new SimpleObjectProperty<>();
    }

    public AccountType getLoginAccountType() {
        return loginAccountType;
    }

    public void setLoginAccountType(AccountType loginAccountType) {
        this.loginAccountType = loginAccountType;
    }

    public ObjectProperty<AdminMenuOption> getAdminSelectedMenuItem() {
        return adminSelectedMenuItem;
    }

    public ObjectProperty<UserMenuOption> getUserSelectedMenuItem() {
        return userSelectedMenuItem;
    }

    public AnchorPane getGamesView() {
        if (gamesView == null) {
            try {
                gamesView = new FXMLLoader(getClass().getResource("/Fxml/Games/Games.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return gamesView;
    }

    public AnchorPane getAddUserView() {
        if (addUserView == null) {
            try {
                addUserView = new FXMLLoader(getClass().getResource("/Fxml/Admin/AddUser.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return addUserView;
    }

    public AnchorPane getUsersView() {
        if (usersView == null) {
            try {
                usersView = new FXMLLoader(getClass().getResource("/Fxml/Admin/Users.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return usersView;
    }

    public AnchorPane getDeleteView() {
        if (deleteView == null) {
            try {
                deleteView = new FXMLLoader(getClass().getResource("/Fxml/Admin/DeleteUser.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deleteView;
    }

    public void showUserWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/User/User.fxml"));
        UserController userController = new UserController();
        loader.setController(userController);
        createStage(loader);
    }

    public void showAdminWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/Admin.fxml"));
        AdminController adminController = new AdminController();
        loader.setController(adminController);
        createStage(loader);
    }

    public void showLoginWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        createStage(loader);
    }

    private void createStage(FXMLLoader loader) {
        Scene scence = null;
        try {
            scence = new Scene(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(scence);
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/images/arcade-game.png"))));
        stage.setResizable(false);
        stage.setTitle("ArcadeFX");
        stage.show();
    }

    public void logout(Stage currentStage) {
        closeStage(currentStage);
        showLoginWindow();
    }

    public void closeStage(Stage stage) {
        stage.close();
    }

}
