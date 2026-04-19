package org.example;

import java.sql.*;

import static org.example.PetShelterGUI.db;

public class DatabaseHandler {
    private static final String URL = "jdbc:sqlite:pets_app.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    public int generatedId;
    public void initialize() {
        String query = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL)";
        String petsTable = "CREATE TABLE IF NOT EXISTS pets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "type TEXT NOT NULL," +
                "name TEXT UNIQUE NOT NULL," +
                "totalMonths INTEGER," +
                "features TEXT NOT NULL," +
                "canTalk INTEGER DEFAULT 0)";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
            stmt.execute(petsTable);
            System.out.println("Database initialization complete.");
        } catch (SQLException e) {
            System.out.println("Initialization error: " + e.getMessage());
        }
    }

    public boolean isValueExists(String tableName, String columnName, String value) {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE " + columnName + " = ?";
        try (Connection conn = getConnection();
             PreparedStatement prStmt = conn.prepareStatement(sql)) {
            prStmt.setString(1, value);
            ResultSet rs = prStmt.executeQuery();
            if (rs.next())
                return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Database check error: " + e.getMessage());
        }
        return false;
    }

    public void registerUser(String username, String password, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement prStmt = conn.prepareStatement(sql)) {
            prStmt.setString(1, username);
            prStmt.setString(2, password);
            prStmt.setString(3, role);
            prStmt.executeUpdate();
        }
    }

    public User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement prStmt = conn.prepareStatement(sql)) {
            prStmt.setString(1, username);
            prStmt.setString(2, password);
            ResultSet rs = prStmt.executeQuery();
            if (rs.next())
                return new User(rs.getString("username"), rs.getString("password"), rs.getString("role"));
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null;
    }


    public void addPet(String type, String name, int totalMonths, String features, boolean canTalk) {
        String sql = "INSERT INTO pets (type, name, totalMonths, features, canTalk) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement prStmt = conn.prepareStatement(sql)) {
            prStmt.setString(1, type);
            prStmt.setString(2, name);
            prStmt.setInt(3, totalMonths);
            prStmt.setString(4, features);
            prStmt.setInt(5, canTalk ? 1 : 0);
            prStmt.executeUpdate();
            ResultSet rs = prStmt.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
            System.out.println("Pet added successfully ^^");
        } catch (SQLException e) {
            System.out.println("Error adding pet: " + e.getMessage());
        }
    }

    public boolean petsByType(String filterType) {
        boolean found = false;
        String sql;
        if (filterType.equals("all")) {
            sql = "SELECT * FROM pets";
        } else {
            sql = "SELECT * FROM pets WHERE LOWER(type) = LOWER(?)";
        } try(Connection conn = getConnection();
        PreparedStatement prStmt = conn.prepareStatement(sql)) {
          if (!filterType.equalsIgnoreCase("all")) {
              prStmt.setString(1, filterType.toLowerCase());
          }
          try (ResultSet rs = prStmt.executeQuery()) {
              while (rs.next()) {
                  found = true;
                  Pet pet = null;
                  int dbId = rs.getInt("id");
                  String dbType = rs.getString("type");
                  String dbName = rs.getString("name");
                  int dbMonths = rs.getInt("totalMonths");
                  String dbFeatures = rs.getString("features");
                  if (dbType.equalsIgnoreCase("dog"))
                      pet = new Dog(dbId, dbName, dbMonths, dbFeatures);
                  else if (dbType.equalsIgnoreCase("cat"))
                      pet = new Cat(dbId, dbName, dbMonths, dbFeatures);
                  else if (dbType.equalsIgnoreCase("parrot")) {
                      boolean canTalk = dbFeatures.contains("canTalk");
                      String specie = dbFeatures.replace("canTalk", "").trim();
                      pet = new Parrot(dbId, dbName, dbMonths, specie, canTalk);
                  }
                  if (pet != null) {
                      System.out.println(pet.getDescription());
                      System.out.println("----------------------------");
                  }
              }
          }
        } catch (SQLException e) {
            System.out.println("DB Error: " + e.getMessage());
        }
        return found;
    }

    static void updatePet(int id, String name, int months, String features, boolean canTalk) {
        String sql = "UPDATE pets SET name = ?, totalMonths = ?, features = ?, canTalk = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement prStmt = conn.prepareStatement(sql)) {
                 prStmt.setString(1, name);
                 prStmt.setInt(2, months);
                 prStmt.setString(3, features);
                 prStmt.setInt(4, canTalk ? 1 : 0);
                 prStmt.setInt(5, id);
                 int rows = prStmt.executeUpdate();
                 if (rows > 0)
                     System.out.println("Success! Pet #" + id + " updated.");
                 else
                     System.out.println("Pet with ID " + id + " not found.");
        } catch (SQLException e) {
            System.out.println("Update error: " + e.getMessage());
        }
    }

    public Pet getPetById(int id) {
        String sql = "SELECT * FROM pets WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement prStmt = conn.prepareStatement(sql)) {
            prStmt.setInt(1, id);
            ResultSet rs = prStmt.executeQuery();
            if (rs.next()) {
                String type = rs.getString("type");
                String name = rs.getString("name");
                int months = rs.getInt("totalMonths");
                String features = rs.getString("features");
                if (type.equalsIgnoreCase("dog")) return new Dog(id, name, months, features);
                else if (type.equalsIgnoreCase("cat")) return new Cat(id, name, months, features);
                else if (type.equalsIgnoreCase("Parrot")) {
                    int talkInt = rs.getInt("canTalk");
                    return new Parrot(id, name, months, features, talkInt == 1);
                }
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return null;
    }

    public static boolean deletePet(int id) {
        String sql = "DELETE FROM pets WHERE id = ?";
        try(Connection conn = getConnection();
            PreparedStatement prStmt = conn.prepareStatement(sql)) {
            prStmt.setInt(1, id);
            int affectedRows = prStmt.executeUpdate();
            return affectedRows > 0;
        } catch(SQLException e) {
            System.out.println("Delete error: " + e.getMessage());
            return false;
        }
    }
}
