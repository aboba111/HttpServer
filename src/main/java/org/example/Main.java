package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Scanner;


public class Main {


    private static void createUser(HttpExchange exchange, DataSource source) throws IOException, SQLException {
        if (exchange.getRequestMethod().equals("POST")) {
            InputStream inputStream = exchange.getRequestBody();
            String result = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
            Object o = null;
            try {
                o = new JSONParser().parse(result);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            JSONObject j = (JSONObject) o;

            String login = (String) j.get("login");
            String password = (String) j.get("password");
            JsonRequest jsonAns=new JsonRequest(new JSONObject());
            JSONObject json = source.createUser(login,password);
            String jsonstr = json.toJSONString();
            byte[] mass = jsonstr.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, mass.length);

            exchange.getResponseBody().write(mass);
            exchange.close();
        }
    }


    private static void hello(HttpExchange exchange, DataSource source) throws IOException, SQLException {
        if (exchange.getRequestMethod().equals("POST")) {

            InputStream inputStream = exchange.getRequestBody();
            String result = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
            Object o = null;
            try {
                o = new JSONParser().parse(result);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            JSONObject j = (JSONObject) o;

            String login = (String) j.get("login");
            String password = (String) j.get("password");
            JsonRequest jsonAns = new JsonRequest(new JSONObject());
            JSONObject json = source.auth(login, password);
            String jsonstr = json.toJSONString();

            byte[] mass = jsonstr.getBytes(StandardCharsets.UTF_8);

            exchange.sendResponseHeaders(200, mass.length);
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            exchange.getResponseBody().write(mass);
            exchange.close();

        }
    }
    private static void  tokenAuth(HttpExchange exchange, DataSource source) throws IOException, SQLException{
        if (exchange.getRequestMethod().equals("POST")) {


            InputStream inputStream = exchange.getRequestBody();
            String result = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
            Object o = null;
            try {
                o = new JSONParser().parse(result);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            JSONObject j = (JSONObject) o;
            String token = (String) j.get("token");
            JSONObject json = source.auth(token);
            String jsonstr = json.toJSONString();
            byte[] mass = jsonstr.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, mass.length);
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            exchange.getResponseBody().write(mass);
            exchange.close();

        }
    }
        private static void update(HttpExchange exchange, DataSource source) throws IOException, SQLException {

            if (exchange.getRequestMethod().equals("POST")) {

                InputStream inputStream = exchange.getRequestBody();
                String result = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
                Object o = null;
                try {
                    o = new JSONParser().parse(result);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                JSONObject j = (JSONObject) o;

                String token = (String) j.get("token");
                Integer rating = (Integer) j.get("rating");
                Integer building_flag1= (Integer) j.get("building_flag1");
                Integer building_flag2= (Integer) j.get("building_flag2");
                Integer building_flag3= (Integer) j.get("building_flag3");
                Integer copper= (Integer) j.get("copper");
                Integer iron= (Integer) j.get("iron");
                Integer gold= (Integer) j.get("gold");
                Integer money= (Integer) j.get("money");
                JSONObject json = source.update(token,rating,building_flag1,building_flag2,building_flag3,copper,iron,gold,money);
                String jsonstr = json.toJSONString();
                byte[] mass = jsonstr.getBytes(StandardCharsets.UTF_8);

                exchange.sendResponseHeaders(200, mass.length);
                exchange.getResponseHeaders().add("Content-Type", "application/json");

                exchange.getResponseBody().write(mass);
                exchange.close();
            }
        }




    public static void main(String[] args) throws IOException, SQLException {

        HttpServer server;
        int port = 8089;
        server = HttpServer.create();
        server.bind(new InetSocketAddress(port), 10000);
        DataSource source = DataSource.getInstance();

        server.createContext("/api", exchange -> {
            try {
                hello(exchange, source);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });

        server.createContext("/update", exchange -> {
            try {
                update(exchange, source);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });



        server.createContext("/create", exchange -> {
            try {
                createUser(exchange, source);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
        server.createContext("/tokenAuth", exchange -> {
            try {
                tokenAuth(exchange, source);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
        server.start();

    }
}