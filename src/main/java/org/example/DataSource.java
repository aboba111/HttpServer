package org.example;

import java.sql.*;


public class DataSource {
   final String DB_URL = "jdbc:postgresql://127.0.0.1:5433/dbUsers";
    final String DB_USER = "postgres";
    final String DB_PASSWORD = "1082423";
    final Connection connection;
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

    public int auth(String login, String password) throws SQLException {
        String query="SELECT * FROM users WHERE username = ? AND passwords = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, password);

        // Выполняем запрос
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()){
           return resultSet.getInt("id");

        }
        else {
            preparedStatement.close();
            resultSet.close();
            return -1;
        }
    }

}
