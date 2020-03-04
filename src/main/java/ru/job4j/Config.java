package ru.job4j;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class Config {
    private final Properties values = new Properties();
    private Connection connection;

    public Connection init() {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("app.properties")) {
            values.load(in);

            String url = values.getProperty("url");
            String username = values.getProperty("username");
            String password = values.getProperty("password");

            this.connection = DriverManager.getConnection(url, username, password);

            return connection;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public String get(String key) {
        return this.values.getProperty(key);
    }
}
