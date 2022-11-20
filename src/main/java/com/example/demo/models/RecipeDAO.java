package com.example.demo.models;

import java.sql.*;
import java.util.*;

public class RecipeDAO {
    private static final RecipeDAO instance = new RecipeDAO();
    private static String url = "jdbc:sqlite:C:/Users/art69/IdeaProjects/javaee_cooking_blog/database.db";

    private RecipeDAO() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static RecipeDAO getInstance() {
        return instance;
    }

    public List<Recipe> allRecipes() {
        List<Recipe> list = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url)) {
            String query = "SELECT * FROM Recipe";
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);

            while (set.next()) {
                String title = set.getString("title");
                String description = set.getString("description");
                int author_id = set.getInt("author_id");

                User author = UserDAO.getInstance().getUserById(author_id);
                list.add(new Recipe(title, description, author));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public void insert(Recipe recipe) {
        try (Connection connection = DriverManager.getConnection(url)) {
            String query = "INSERT INTO Recipe(title, description, author_id) " +
                    "VALUES(?, ?, (SELECT id FROM User WHERE email = ?))";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, recipe.getTitle());
            statement.setString(2, recipe.getDescription());
            statement.setString(3, recipe.getUser().getEmail());
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public List<Recipe> findOnPrefix(String user_prefix, String recipi_prefix) {
        List<Recipe> list = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url)) {
            String query = "SELECT * FROM Recipe JOIN User ON " +
                    "(recipe.author_id = User.id) WHERE title LIKE '" + recipi_prefix + "%' AND email LIKE '" + user_prefix + "%'" +
                    " order by recipe.title";
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);

            while (set.next()) {
                String title = set.getString("title");
                String description = set.getString("description");
                int author_id = set.getInt("author_id");

                User author = UserDAO.getInstance().getUserById(author_id);//получаем юзера чтобы передать в лист рецептов
                list.add(new Recipe(title, description, author));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }
}