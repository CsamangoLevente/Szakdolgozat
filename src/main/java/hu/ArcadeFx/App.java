package hu.ArcadeFx;

import hu.ArcadeFx.Models.Model;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        Model.getInstance().getView().showLoginWindow();
    }
}
