package hu.ArcadeFx.Models;

import hu.ArcadeFx.Views.AccountType;
import hu.ArcadeFx.Views.View;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.time.LocalDate;


public class Model {
    private static Model model;
    private final View view;
    private final Database database;
    private AccountType loginAccountType = AccountType.GAMER;
    private User user;
    private boolean userLoginSuccess;
    private final ObservableList<User> players;


    private Model() {
        this.view = new View();
        this.database = new Database();
        this.userLoginSuccess = false;
        this.user = new User("","", "", null);
        this.players = FXCollections.observableArrayList();
    }

    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }

    public View getView() {
        return view;
    }

    public Database getDatabase() {return database;}

    public AccountType getLoginAccountType() {return loginAccountType;}

    public void setLoginAccountType(AccountType loginAccountType) {this.loginAccountType = loginAccountType;}

    // User Segédprogramok
    public boolean getUserLoginSuccess() {return this.userLoginSuccess;}

    public void setUserLoginSuccess(boolean userLoginSuccess) {
        this.userLoginSuccess = userLoginSuccess;
    }

    public User getUser() {return user;}

    public void evaluateUserCred(String user_id, String password) {
        ResultSet resultSet = database.getUserData(user_id, password);
        if (resultSet == null) {
            System.out.println("Adatbázis hiba: resultSet null.");
            return;
        }
        try {
            this.userLoginSuccess = resultSet.next();
            if (this.userLoginSuccess) {
                this.user.firstnameProperty().set(resultSet.getString("FirstName"));
                this.user.lastnameProperty().set(resultSet.getString("LastName"));
                this.user.userIdProperty().set(resultSet.getString("UserID"));
                String[] dateParts = resultSet.getString("Date").split("-");
                LocalDate date = LocalDate.of(
                        Integer.parseInt(dateParts[0]),
                        Integer.parseInt(dateParts[1]),
                        Integer.parseInt(dateParts[2])
                );
                this.user.dateCreatedProperty().set(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.userLoginSuccess = false;
        }
    }

    public ObservableList<User> getPlayersList() {return players;}

    public void setPlayers() {
        ResultSet result = database.getAllPlayers();
        try {
            while (result.next()) {
                getPlayerData(players, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObservableList<User> search(String userID) {
        ObservableList<User> result = FXCollections.observableArrayList();
        ResultSet resultSet = database.searchPlayers(userID);
        try {
            if (resultSet.next()) {
                getPlayerData(result, resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void getPlayerData(ObservableList<User> result, ResultSet resultSet) {
        try {
            String firstName = resultSet.getString("FirstName");
            String lastName = resultSet.getString("LastName");
            String userId = resultSet.getString("UserID");
            String[] dateParts = resultSet.getString("Date").split("-");
            LocalDate date = LocalDate.of(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[2]));
            result.add(new User(firstName, lastName, userId, date));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deletePlayer(User user) {
        String userId = user.userIdProperty().get();
        boolean deleted = database.deletePlayer(userId);
        if (deleted) {
            players.removeIf(player -> player.userIdProperty().get().equals(userId)
            );
        }
        return deleted;
    }

    public boolean createPlayer(String firstName, String lastName, String userId, String password, LocalDate date) {
        boolean created = database.createPlayer(firstName, lastName, userId, password, date);
        if (created) {
            players.add(new User(firstName, lastName, userId, date));
        }
        return created;
    }
}
