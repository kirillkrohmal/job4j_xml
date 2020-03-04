package ru.job4j;

import java.sql.*;
import java.util.Collections;
import java.util.List;

public class StoreSQL implements AutoCloseable {
    private Config config;
    private Connection connect;

    public StoreSQL(Config config) {
        this.config = config;
        connect = config.init();
    }

    public void generate(int size) {
        for (int i = 0; i < size; i++) {
            String s1 = "INSERT INTO entry(i) VALUES (?)";

            try (PreparedStatement statement = connect.prepareStatement(s1);) {
                statement.setString(1, s1);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String findAll() {
        String s1 = "SELECT field FROM items";

        try (PreparedStatement statement = connect.prepareStatement(s1);) {
            statement.setString(1, s1);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return s1;
    }

    public static void createNewDatabase(String fileName) throws SQLException {

        String url = "jdbc:sqlite:C:/sqlite/db/" + fileName;

        Connection conn = DriverManager.getConnection(url);
        Statement statement = conn.createStatement();

        try {
            String sql = "CREATE TABLE items " + "(field INTEGER not NULL)";

            statement.executeUpdate(sql);
            System.out.println("Table successfully created...");

            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public List<Entry> load() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void close() throws Exception {
        if (connect != null) {
            connect.close();
        }
    }

    public static void main(String[] args) {
        try {
            createNewDatabase("test.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
