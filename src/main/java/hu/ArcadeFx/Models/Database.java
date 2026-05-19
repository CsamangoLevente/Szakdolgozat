package hu.ArcadeFx.Models;

import hu.ArcadeFx.Views.AccountType;

import java.sql.*;
import java.time.LocalDate;

public class Database {
    private Connection connection;

    public Database() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:gamefx.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getUserData(String user_id, String password) {
        PreparedStatement statement;
        String query;
        try {
            if (Model.getInstance().getView().getLoginAccountType() == AccountType.GAMER) {
                query = "SELECT * FROM Players WHERE UserID = ? AND Password = ?";
            } else {
                query = "SELECT * FROM Admins WHERE UserID = ? AND Password = ?";
            }
            statement = this.connection.prepareStatement(query);
            statement.setString(1, user_id);
            statement.setString(2, password);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean createPlayer(String firstName, String lastName, String user_id, String password, LocalDate date) {
        String query = """
        INSERT INTO Players (FirstName, LastName, UserID, Password, Date)
        VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, user_id);
            statement.setString(4, password);
            statement.setString(5, date.toString());

            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getLastPlayerId() {
        Statement statement;
        ResultSet result;
        int id = 0;
        try {
            statement = this.connection.createStatement();
            result = statement.executeQuery("SELECT * FROM sqlite_sequence WHERE name = 'Players';");
            id = result.getInt("seq");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public ResultSet getAllPlayers() {
        Statement statement;
        ResultSet result = null;
        try {
            statement = this.connection.createStatement();
            result = statement.executeQuery("SELECT * FROM Players;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ResultSet searchPlayers(String userId) {
        String query = "SELECT * FROM Players WHERE UserID = ?";
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, userId);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deletePlayer(String userId) {
        String query = "DELETE FROM Players WHERE UserID = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, userId);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
