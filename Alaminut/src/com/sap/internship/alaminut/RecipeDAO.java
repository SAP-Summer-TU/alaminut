package com.sap.internship.alaminut;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

/**
 * Data access object encapsulating
 */

public class RecipeDAO {
	private DataSource dataSource;

    /**
     * Create new data access object with data source.
     */
    public RecipeDAO(DataSource newDataSource) throws SQLException {
        setDataSource(newDataSource);
    }

    /**
     * Get data source which is used for the database operations.
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Set data source to be used for the database operations.
     */
    public void setDataSource(DataSource newDataSource) throws SQLException {
        this.dataSource = newDataSource;
        checkTable(); 
    }
    
    /**
     * Add new recipe.
     */
    public void addRecipe(Recipe recipe) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            PreparedStatement pstmt = connection
                    .prepareStatement("INSERT INTO RECIPES (ID, NAME, HOWTOMAKE, PICTURE) VALUES (?, ?, ?, ?)"); //**
            pstmt.setString(1, recipe.getId());
            pstmt.setString(2, recipe.getName());
            pstmt.setString(3, recipe.getHowToMake());
            pstmt.setString(4, recipe.getPicture());	//**
            pstmt.executeUpdate();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    /**
     * vra6ta ID na recepta, koqto e bila tarsena po ime.
     */
    
    public List<Recipe> selectAllRecipes(ConnProductDAO connProductDAO) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement pstmt = connection
                    .prepareStatement("SELECT * FROM RECIPES");
            ResultSet rs = pstmt.executeQuery();
            ArrayList<Recipe> list = new ArrayList<Recipe>();
            while (rs.next()) {
                Recipe r = new Recipe();
                r.setId(rs.getString("ID"));
                r.setName(rs.getString("NAME"));
                r.setHowToMake(rs.getString("HOWTOMAKE"));
                r.setPicture(rs.getString("PICTURE"));	//**
                
                List<String> recipeProduct = connProductDAO.getProducts(r);
                
                r.setProducs(recipeProduct);
                
                list.add(r);
            }
            return list;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    
    public int getRecipeId(Recipe recipe) throws SQLException {
        Connection connection = dataSource.getConnection();
        int id=0;
        try {
            PreparedStatement pstmt = connection
                    .prepareStatement("SELECT RECIPES.ID FROM RECIPES WHERE RECIPES.NAME = ?");
            pstmt.setString(1, recipe.getName());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                id=rs.getInt("ID");
            }
            return id;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    /**
     * vra6ta recepta, koqto e bila tarsena po id.
     */
    public Recipe getRecipe(String id) throws SQLException {
        Connection connection = dataSource.getConnection();
        Recipe r = new Recipe();
        try {
            PreparedStatement pstmt = connection
                    .prepareStatement("SELECT RECIPES.NAME, RECIPES.HOWTOMAKE, RECIPES.PICTURE"  //**
                    		+ "FROM RECIPES WHERE RECIPES.ID = ?");
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                r.setId(id);
                r.setName(rs.getString("NAME"));
                r.setHowToMake(rs.getString("HOWTOMAKE"));
                r.setHowToMake(rs.getString("PICTURE"));  //**
            }
            return r;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    /**
     * Check if the recipes table already exists and create it if not.
     */
    private void checkTable() throws SQLException {
        Connection connection = null;
        
        try {
            connection = dataSource.getConnection();
            /*PreparedStatement pstmt = connection
                    .prepareStatement("DROP TABLE CONNPRODUCT");	//**
            pstmt.executeUpdate();
            PreparedStatement pstmt = connection
                    .prepareStatement("DROP TABLE PRODUCTS");	//**
            pstmt.executeUpdate();
            pstmt = connection
                    .prepareStatement("DROP TABLE RECIPES");	//**
            pstmt.executeUpdate();*/
            
            
           if (!existsTable(connection)) {
                createTable(connection);
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    /**
     * Check if the recipes table already exists.
     */
    private boolean existsTable(Connection conn) throws SQLException {
    	
    	DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getTables(null, null, "RECIPES", null);
        while (rs.next()) {
            String name = rs.getString("TABLE_NAME");
            if (name.equals("RECIPES")) {
                return true;
            }
        }
        return false;
    }
    /**
     * Create the recipes table.
     */
    
    private void createTable(Connection connection) throws SQLException {
    	
    	PreparedStatement pstmt = connection
                .prepareStatement("CREATE TABLE RECIPES "
                        + "(ID VARCHAR(255) PRIMARY KEY, "
                        + "NAME VARCHAR(255) UNIQUE NOT NULL,"
                        + "HOWTOMAKE VARCHAR(2500) NOT NULL,"
                        + "PICTURE VARCHAR(255))");	//**
        pstmt.executeUpdate();
    }
    

}
