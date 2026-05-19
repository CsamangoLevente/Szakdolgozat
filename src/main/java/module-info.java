module hu.ArcadeFx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires de.jensd.fx.glyphs.fontawesome;
    requires de.jensd.fx.glyphs.commons;


    opens hu.ArcadeFx to javafx.fxml;
    opens hu.ArcadeFx.Controllers to javafx.fxml;
    opens hu.ArcadeFx.Controllers.User to javafx.fxml;
    opens hu.ArcadeFx.Controllers.Admin to javafx.fxml;

    exports hu.ArcadeFx;
    exports hu.ArcadeFx.Views;
    exports hu.ArcadeFx.Controllers;
    exports hu.ArcadeFx.Network.PacMan;
    exports hu.ArcadeFx.Models;
    exports hu.ArcadeFx.Models.Games;
    exports hu.ArcadeFx.Controllers.User;
    exports hu.ArcadeFx.Controllers.Admin;
    exports hu.ArcadeFx.Controllers.Games;
    exports hu.ArcadeFx.Models.Games.PacMan;
    opens hu.ArcadeFx.Controllers.Games to javafx.fxml;
    opens hu.ArcadeFx.Models to javafx.fxml;
    exports hu.ArcadeFx.Controllers.Games.FlappyBird;
    opens hu.ArcadeFx.Controllers.Games.FlappyBird to javafx.fxml;
    exports hu.ArcadeFx.Models.Games.FlappyBird;
    opens hu.ArcadeFx.Models.Games to javafx.fxml;
    exports hu.ArcadeFx.Network;
}
