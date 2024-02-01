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
        String query = "SELECT u.users_id, u.login, u.password, u.rating,\n" +
                "b.buildings_id, b.building_flag1, b.building_flag2, b.building_flag3,\n" +
                "r.resources_id, r.copper, r.iron, r.gold, r.money\n" +
                "FROM users u\n" +
                "LEFT JOIN buildings b ON u.users_id = b.users_id\n" +
                "LEFT JOIN resources r ON u.users_id = r.users_id\n" +
                "WHERE u.login = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, login);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            boolean passwordMatch = BCrypt.checkpw(password, resultSet.getString("password"));
            if (passwordMatch) {
                int rating = resultSet.getInt("rating");
                int building_flag1 = resultSet.getInt("building_flag1");
                int building_flag2 = resultSet.getInt("building_flag2");
                int building_flag3 = resultSet.getInt("building_flag3");
                int copper = resultSet.getInt("copper");
                int iron = resultSet.getInt("iron");
                int gold = resultSet.getInt("gold");
                int money = resultSet.getInt("money");
                String token = MyJwt.getJwt(login, password);
                return new JsonRequest(new JSONObject()).authSuccess(rating, building_flag1,
                        building_flag2, building_flag3, copper, iron, gold, money, token);
            } else {
                return new JsonRequest(new JSONObject()).authError();
            }

        } else {
            preparedStatement.close();
            resultSet.close();
            return new JsonRequest(new JSONObject()).authError();
        }
    }

    public JSONObject auth(String token) throws SQLException {
        if (token == null || token.equals("zero")) {
            return new JsonRequest(new JSONObject()).authError();
        }
        Jws<Claims> jwsToken = MyJwt.parseJwt(token);
        if (jwsToken == null) {
            return new JsonRequest(new JSONObject()).authError();
        }
        String tokenStr = jwsToken.toString();
        int indexStart = tokenStr.indexOf("login") + 6;
        int indexEnd = tokenStr.indexOf(",", indexStart);
        String login = tokenStr.substring(indexStart, indexEnd);
        indexStart = tokenStr.indexOf("password") + 9;
        indexEnd = tokenStr.indexOf(",", indexStart);
        String password = tokenStr.substring(indexStart, indexEnd);
        String query = "SELECT u.users_id, u.login, u.password, u.rating,\n" +
                "b.buildings_id, b.building_flag1, b.building_flag2, b.building_flag3,\n" +
                "r.resources_id, r.copper, r.iron, r.gold, r.money\n" +
                "FROM users u\n" +
                "LEFT JOIN buildings b ON u.users_id = b.users_id\n" +
                "LEFT JOIN resources r ON u.users_id = r.users_id\n" +
                "WHERE u.login = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, login);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            boolean passwordMatch = BCrypt.checkpw(password, resultSet.getString("password"));
            if (passwordMatch) {
                int rating = resultSet.getInt("rating");
                int building_flag1 = resultSet.getInt("building_flag1");
                int building_flag2 = resultSet.getInt("building_flag2");
                int building_flag3 = resultSet.getInt("building_flag3");
                int copper = resultSet.getInt("copper");
                int iron = resultSet.getInt("iron");
                int gold = resultSet.getInt("gold");
                int money = resultSet.getInt("money");
                return new JsonRequest(new JSONObject()).authSuccess(rating, building_flag1,
                        building_flag2, building_flag3, copper, iron, gold, money, token);
            } else {
                return new JsonRequest(new JSONObject()).authError();
            }

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
        if (resultSet.next()) {
            return new JsonRequest(new JSONObject()).authError();
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        connection.setAutoCommit(false);
        try {
            String insertUserSQL = "INSERT INTO users (login, password, rating) VALUES (?, ?, 1) RETURNING users_id";
            try (PreparedStatement pstmt = connection.prepareStatement(insertUserSQL)) {
                pstmt.setString(1, login);
                pstmt.setString(2, hashedPassword);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int newUsersId = rs.getInt(1);
                    String insertBuildingsSQL = "INSERT INTO buildings (building_flag1, building_flag2, building_flag3, users_id) VALUES (0, 0, 0, ?)";
                    try (PreparedStatement pstmtBuildings = connection.prepareStatement(insertBuildingsSQL)) {
                        pstmtBuildings.setInt(1, newUsersId);
                        pstmtBuildings.executeUpdate();
                    }
                    String insertResourcesSQL = "INSERT INTO resources (copper, iron, gold, money, users_id) VALUES (100, 150, 200, 60000000, ?)";
                    try (PreparedStatement pstmtResources = connection.prepareStatement(insertResourcesSQL)) {
                        pstmtResources.setInt(1, newUsersId);
                        pstmtResources.executeUpdate();
                    }
                }
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
        return new JsonRequest(new JSONObject()).authSuccess(1, 0, 0, 0, 100, 150, 200, 60000000, MyJwt.getJwt(login, password));

    }

    public JSONObject update(String token ,int rating, int building_flag1, int building_flag2 ,
    int building_flag3, int copper, int iron ,
    int gold , int money ) throws SQLException {
        if (token == null || token.equals("zero")) {
            return new JsonRequest(new JSONObject()).authError();
        }
        Jws<Claims> jwsToken = MyJwt.parseJwt(token);
        if (jwsToken == null) {
            return new JsonRequest(new JSONObject()).authError();
        }
        String tokenStr = jwsToken.toString();
        int indexStart = tokenStr.indexOf("login") + 6;
        int indexEnd = tokenStr.indexOf(",", indexStart);
        String login = tokenStr.substring(indexStart, indexEnd);
        indexStart = tokenStr.indexOf("password") + 9;
        indexEnd = tokenStr.indexOf(",", indexStart);
        String password = tokenStr.substring(indexStart, indexEnd);
        String query = "SELECT * FROM users WHERE login = ?";
        PreparedStatement preparedStatementLogin = connection.prepareStatement(query);
        preparedStatementLogin.setString(1, login);
        ResultSet resultSet = preparedStatementLogin.executeQuery();
        if (resultSet.next()) {
            boolean passwordMatch = BCrypt.checkpw(password, resultSet.getString("password"));
            if(passwordMatch) {
                int id=resultSet.getInt("users_id");
                String sql1 = "UPDATE users SET rating=? WHERE users_id=?;";
                PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
                preparedStatement1.setInt(1, rating);
                preparedStatement1.setInt(2, id);
                preparedStatement1.executeUpdate();
                String sql2 = "UPDATE buildings SET building_flag1=? , building_flag2=? ,building_flag3=?  WHERE users_id=?;";
                PreparedStatement preparedStatement2 = connection.prepareStatement(sql1);
                preparedStatement2.setInt(1, building_flag1);
                preparedStatement2.setInt(2, building_flag2);
                preparedStatement2.setInt(3, building_flag3);
                preparedStatement2.setInt(4, id);
                preparedStatement2.executeUpdate();
                String sql3 = "UPDATE resources SET copper=? ,iron=?, gold=?, money=?  WHERE users_id=?;";
                PreparedStatement preparedStatement3 = connection.prepareStatement(sql1);
                preparedStatement3.setInt(1, copper);
                preparedStatement3.setInt(2, iron);
                preparedStatement3.setInt(3, gold);
                preparedStatement3.setInt(4, money);
                preparedStatement3.setInt(5, id);
                preparedStatement3.executeUpdate();
                return  new JsonRequest(new JSONObject()).updateOk();
            }else {
                return  new JsonRequest(new JSONObject()).updateError();
            }
        }else {
            return  new JsonRequest(new JSONObject()).updateError();
        }
    }
}
