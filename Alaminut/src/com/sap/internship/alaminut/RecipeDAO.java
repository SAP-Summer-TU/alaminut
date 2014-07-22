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
                    .prepareStatement("INSERT INTO RECEPTI (ID, NAME, OPISANIE) VALUES (?, ?, ?)");
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, recipe.getName());
            pstmt.setString(3, recipe.getHowToMake());
            pstmt.executeUpdate();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    
    /**
     * Get specific recipe.		UNDER CONSTRUCTION!!	
     	
    public void getSpecificRecipe(Person person) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            PreparedStatement pstmt = connection
                    .prepareStatement("INSERT INTO PERSONS (ID, FIRSTNAME, LASTNAME) VALUES (?, ?, ?)");
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, person.getFirstName());
            pstmt.setString(3, person.getLastName());
            pstmt.executeUpdate();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    /**
     * Get all persons from the table.
     
    public List<Person> selectAllPersons() throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement pstmt = connection
                    .prepareStatement("SELECT ID, FIRSTNAME, LASTNAME FROM PERSONS");
            ResultSet rs = pstmt.executeQuery();
            ArrayList<Person> list = new ArrayList<Person>();
            while (rs.next()) {
                Person p = new Person();
                p.setId(rs.getString(1));
                p.setFirstName(rs.getString(2));
                p.setLastName(rs.getString(3));
                list.add(p);
            }
            return list;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }	*/
    
    /**
     * vra6ta ID na recepta, koqto e bila tarsena po ime.
     */
    public int getRecipeByName(Recipe recipe) throws SQLException {
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
     * Check if the recipes table already exists and create it if not.
     */
    private void checkTable() throws SQLException {
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
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
                        + "(id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
                        + "name VARCHAR(255) UNIQUE NOT NULL,"
                        + "howtomake VARCHAR(255) NOT NULL)");		//ENGINE=InnoDB ???????
        pstmt.executeUpdate();
    }
}