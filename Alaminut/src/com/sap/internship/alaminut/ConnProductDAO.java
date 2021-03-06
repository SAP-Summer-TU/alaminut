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

public class ConnProductDAO {

	private DataSource dataSource;

    /**
     * Create new data access object with data source.
     */
    public ConnProductDAO(DataSource newDataSource) throws SQLException {
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
     * vra6ta produktite na recepta, na koqto se znae id-to.	VARIANT KOITO VRA6TA STRING
     */
    public String getProductsString(Recipe recipe) throws SQLException {
        Connection connection = dataSource.getConnection();
        String temp=null;
        ArrayList<String> list = new ArrayList<String>();
        try {
            PreparedStatement pstmt = connection
                    .prepareStatement("SELECT PRODUCTS.NAME "
                    		+ "FROM PRODUCTS JOIN CONNPRODUCT ON"
                    		+ "PRODUCTS.ID=CONNPRODUCT.PRODUCT_ID JOIN RECIPES ON"
                    		+ "CONNPRODUCT.RECIPE_ID = ?"
                    		+ "GROUP BY PRODUCTS.ID");
            pstmt.setString(1, recipe.getId());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
            	temp=rs.getString("NAME");
            	list.add(temp);
            }
            
            return listToString(list);	//elementite na lista stavat edin String
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    /**
     * vra6ta produktite na recepta, na koqto se znae id-to.	VARIANT KOITO VRA6TA LIST
     */
    public List<String> getProducts(Recipe recipe) throws SQLException {
        Connection connection = dataSource.getConnection();
        String temp=null;
        List<String> list = new ArrayList<String>();
        try {
            PreparedStatement pstmt = connection
                    .prepareStatement("SELECT PRODUCTS.NAME FROM PRODUCTS "
                    		+ "WHERE PRODUCTS.ID IN( SELECT PRODUCT_ID "
                    		+ "FROM CONNPRODUCT WHERE RECIPE_ID = ? )");
            pstmt.setString(1, recipe.getId());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
            	String productName = rs.getString("NAME");
                list.add(productName);
            }
            
            return list;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    /**
     * Get specific recipe.	
     */	
    public List<Recipe> getSpecificRecipe(List<String> prodList) throws SQLException {	//trqbva da polu4i String ot produkti
        Connection connection = dataSource.getConnection();
        
        String sqlQuery = "SELECT RECIPES.ID, RECIPES.NAME, RECIPES.HOWTOMAKE, RECIPES.PICTURE " //**
        		+ "FROM RECIPES "
        		+ "WHERE RECIPES.ID IN ("
        		+ "SELECT RECIPE_ID FROM CONNPRODUCT "
        		+ "WHERE PRODUCT_ID IN ("
        		+ "SELECT PRODUCTS.ID FROM PRODUCTS "
        		+ "WHERE PRODUCTS.NAME IN (?)"
        		+ "))";
        
        sqlQuery = editParams(sqlQuery, prodList.size());
        
        try {
            PreparedStatement pstmt = connection
                    .prepareStatement(sqlQuery);
            int count = 1;
            for (String string : prodList) {
            	pstmt.setString(count, string);
            	count++;
			}
            
            ResultSet rs = pstmt.executeQuery();
            ArrayList<Recipe> list = new ArrayList<Recipe>();
            while (rs.next()) {
                Recipe r = new Recipe(rs.getString("ID"), 
                		rs.getString("NAME"), rs.getString("HOWTOMAKE"), rs.getString("PICTURE")) ;	//**
                r.setProducs(this.getProducts(r));
                list.add(r);
            }
            return list;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    
    private String editParams(String sqlQuery, int size) {
		StringBuilder sb = new StringBuilder(sqlQuery);
		
		int indexOfParam = sb.indexOf("?");
		
		for (int i = 0; i < size - 1; i++) {
			sb.insert(indexOfParam + 1, ", ?");
		}
		
		return sb.toString();
	}

	public void insertConnection(String recipe_id, String product_id) throws SQLException{
    	Connection connection = dataSource.getConnection();

        try {
            PreparedStatement pstmt = connection
                    .prepareStatement("INSERT INTO CONNPRODUCT (RECIPE_ID, PRODUCT_ID) VALUES (?, ?)");
            pstmt.setString(1, recipe_id);
            pstmt.setString(2, product_id);
            pstmt.executeUpdate();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    
    /**
     * Check if the connection_product table already exists and create it if not.
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
     * Check if the connection_product table already exists.
     */
    private boolean existsTable(Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getTables(null, null, "CONNPRODUCT", null);
        while (rs.next()) {
            String name = rs.getString("TABLE_NAME");
            if (name.equals("CONNPRODUCT")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create the connection_product table.
     */
    private void createTable(Connection connection) throws SQLException {
        PreparedStatement pstmt = connection
                .prepareStatement("CREATE TABLE CONNPRODUCT "
                        + "(RECIPE_ID VARCHAR(255), "
                        + "PRODUCT_ID VARCHAR(255))");
        pstmt.executeUpdate();
    }
    /**
     * zapisva elementite na masiv v String.
     */
    /*public static String listToString(ArrayList<String> list){
		String listString = "";

		for (String s : list)
		{
		    listString += s + "\n";	//separator
		}
		return listString;
	}*/
    
    
    public static String listToString(List<String> list){
		String listString = "";

		for (String s : list)
		{
			if (s.length() > 0)
			    listString += "'" + s + "',";	//separator
		}
		if (listString.length() > 0) {
			listString = listString.substring(1, listString.length()-2);
		  }
		return listString;
	}
}