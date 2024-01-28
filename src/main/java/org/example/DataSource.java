package org.example;

import org.json.simple.JSONObject;

import java.sql.*;


public class DataSource {
    final String DB_URL = "jdbc:postgresql://127.0.0.1:5433/dbUsers";
    final String DB_USER = "postgres";
    final String DB_PASSWORD = "1082423";
    Connection connection;
    private static final DataSource INSTANCE;

    static {
        try {
            INSTANCE = new DataSource();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private DataSource() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static DataSource getInstance() {
        return INSTANCE;
    }

    public JSONObject auth(String login, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE login = ? AND userPassword = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, password);

        // Выполняем запрос
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return new JsonRequest(new JSONObject()).authSuccess(resultSet.getInt("diamond"), resultSet.getInt("gold"));

        } else {
            preparedStatement.close();
            resultSet.close();
            return new JsonRequest(new JSONObject()).authError();
        }
    }

    public JSONObject createUser(String login, String password) throws SQLException {
        String sql = "INSERT INTO users (login, userPassword, diamond, gold) VALUES ('l', 'jjjdksl',100, 100);";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
        return new JsonRequest(new JSONObject()).createUser(100,100);
    }
    public void update(String login, String password, int countGold, int countDiamond) throws SQLException {
        String sql = "UPDATE users SET  diamond = ?, gold = ? WHERE login = ? AND password = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, countDiamond);
        preparedStatement.setInt(2, countGold);
        preparedStatement.setString(3, login);
        preparedStatement.setString(4, password);
        preparedStatement.executeUpdate();
    }


}
