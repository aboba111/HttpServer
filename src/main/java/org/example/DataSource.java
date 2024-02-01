package org.example;

import org.json.simple.JSONObject;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

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
        String query = "SELECT * FROM users WHERE login = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, login);
        System.out.println("007");
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            boolean passwordMatch = BCrypt.checkpw(password, resultSet.getString("userPassword"));
            return new JsonRequest(new JSONObject()).authSuccess(resultSet.getInt("diamond"), resultSet.getInt("gold"),MyJwt.getJwt(login,password));

        } else {
            preparedStatement.close();
            resultSet.close();
            return new JsonRequest(new JSONObject()).authError();
        }
    }
    public JSONObject auth(String token) throws SQLException {
        if(token==null||token.equals("")){
            System.out.println("666");
            return new JsonRequest(new JSONObject()).authError();
        }
        Jws<Claims> jwsToken=MyJwt.parseJwt(token);
        if(jwsToken==null){
            return new JsonRequest(new JSONObject()).authError();
        }
        String tokenStr=jwsToken.toString();
        int indexStart=tokenStr.indexOf("login")+6;
        int indexEnd=tokenStr.indexOf(",",indexStart);
        String login= tokenStr.substring(indexStart,indexEnd);
        System.out.println("5535"+login);
        indexStart=tokenStr.indexOf("password")+9;
        indexEnd=tokenStr.indexOf(",",indexStart);
        String password= tokenStr.substring(indexStart,indexEnd);
        System.out.println("5555"+password);
        String query = "SELECT * FROM users WHERE login = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, login);
        System.out.println("007");
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            boolean passwordMatch = BCrypt.checkpw(password, resultSet.getString("userPassword"));
            return new JsonRequest(new JSONObject()).authSuccess(resultSet.getInt("diamond"), resultSet.getInt("gold"),MyJwt.getJwt(login,password));

        } else {
            preparedStatement.close();
            resultSet.close();
            return new JsonRequest(new JSONObject()).authError();
        }
    }
    public JSONObject createUser(String login, String password) throws SQLException {

        String query = "SELECT * FROM users WHERE login = ?";
        PreparedStatement preparedStatementLogin = connection.prepareStatement(query);
        preparedStatementLogin.setString(1, login);
        ResultSet resultSet = preparedStatementLogin.executeQuery();
        if (resultSet.next()){
            return new JsonRequest(new JSONObject()).authError();
        }



        String sql = "INSERT INTO users (login, userPassword, diamond, gold) VALUES ( ? , ? ,100, 100);";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, hashedPassword);
        preparedStatement.executeUpdate();
        return new JsonRequest(new JSONObject()).createUser(100,100,MyJwt.getJwt(login,password));
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
