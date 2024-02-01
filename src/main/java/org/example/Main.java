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

            // Преобразование InputStream в строку
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

            System.out.println("hello2209");
            JsonRequest jsonAns=new JsonRequest(new JSONObject());
            JSONObject json = source.createUser(login,password);
            System.out.println("hello2209");
            String jsonstr = json.toJSONString();
            byte[] mass = jsonstr.getBytes(StandardCharsets.UTF_8);
            System.out.println(jsonstr);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, mass.length);

            exchange.getResponseBody().write(mass);
            exchange.close();


            System.out.println(login + " " + password);


        }
    }


    private static void hello(HttpExchange exchange, DataSource source) throws IOException, SQLException {
        if (exchange.getRequestMethod().equals("POST")) {
            System.out.println("Post");
            System.out.println("hello");

            InputStream inputStream = exchange.getRequestBody();

            // Преобразование InputStream в строку
            String result = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
            System.out.println(result);
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



           // String jsonstr="{\"password\":\"password\",\"login\":\"login\"}";

            byte[] mass = jsonstr.getBytes(StandardCharsets.UTF_8);
            System.out.println(jsonstr);
            exchange.sendResponseHeaders(200, mass.length);
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            exchange.getResponseBody().write(mass);
            exchange.close();




            System.out.println(login + " " + password);


        }
    }
    private static void  tokenAuth(HttpExchange exchange, DataSource source) throws IOException, SQLException{
        if (exchange.getRequestMethod().equals("POST")) {
            System.out.println("Post1");
            System.out.println("hello");

            InputStream inputStream = exchange.getRequestBody();

            // Преобразование InputStream в строку
            String result = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();

            System.out.println(result+"333");
            Object o = null;
            try {
                o = new JSONParser().parse(result);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            JSONObject j = (JSONObject) o;
            System.out.println("3123");

            String token = (String) j.get("token");
            System.out.println(token);

            JSONObject json = source.auth(token);
            String jsonstr = json.toJSONString();
            System.out.println("76543");


            // String jsonstr="{\"password\":\"password\",\"login\":\"login\"}";

            byte[] mass = jsonstr.getBytes(StandardCharsets.UTF_8);
            System.out.println(jsonstr);
            exchange.sendResponseHeaders(200, mass.length);
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            exchange.getResponseBody().write(mass);
            exchange.close();

        }
    }
        private static void update(HttpExchange exchange, DataSource source) throws IOException, SQLException {

            if (exchange.getRequestMethod().equals("POST")) {
                System.out.println("hello");
                InputStream inputStream = exchange.getRequestBody();

                // Преобразование InputStream в строку
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
                Integer diamond= (Integer) j.get("diamond");
                Integer gold= (Integer) j.get("gold");

                // String json="{\"id\":\"512341\"}";

                JsonRequest jsonAns = new JsonRequest(new JSONObject());
                source.update(login, password,gold,diamond);
                System.out.println(login + " " + password);
            }
        }




    public static void main(String[] args) throws IOException, SQLException {

        HttpServer server;
        int port = 8089;
        server = HttpServer.create();
        server.bind(new InetSocketAddress(port), 10000);
        DataSource source = DataSource.getInstance();
        /*
        source.auth(MyJwt.getJwt("heeelp", "wwwo"));
        String hashedPassword = BCrypt.hashpw("rfhjy", BCrypt.gensalt());
        System.out.println(hashedPassword.length());

         */

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