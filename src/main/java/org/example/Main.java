package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class Main {

        private static void hello(HttpExchange exchange) throws IOException{

            if(exchange.getRequestMethod().equals("POST")){
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
               // String json="{\"id\":\"512341\"}";
                JSONObject json=new JSONObject();
                json.put("id","5");
                String jsonstr=json.toJSONString();
                byte[] mass=jsonstr.getBytes(StandardCharsets.UTF_8);
                System.out.println(jsonstr);
                exchange.getResponseHeaders().add("Content-Type","application/json");
                exchange.sendResponseHeaders(200,mass.length);
                exchange.getResponseBody().write(mass);





                System.out.println(login+" "+password);







            }

        }
        public static void main(String[] args) throws IOException {

            HttpServer server;
            int port=8089;
            server=HttpServer.create();
            server.bind(new InetSocketAddress(port),10000);
            server.createContext("/api", Main::hello);
            server.start();

    }
}