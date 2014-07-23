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

public class ProductsDAO {
	private DataSource dataSource;
	
	/**
     * Create new data access object with data source.
     */
    public ProductsDAO(DataSource newDataSource) throws SQLException {
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
     * vra6ta true - ako produkta go ima v bazata
     * vra6ta false - ako produkta go nqma
     */
    public boolean haveProduct(String product) throws SQLException {
        Connection connection = dataSource.getConnection();
        String id = "";
        try {
            PreparedStatement pstmt = connection
                    .prepareStatement("SELECT PRODUCTS.ID FROM PRODUCTS WHERE PRODUCTS.NAME = ?");
            pstmt.setString(1, product);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                id=rs.getString("ID");
            }
            if(!(id.isEmpty())) return true;
            else return false;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    /**
     * Add a product to the table.
     */
    public void addProduct(Products product) throws SQLException {
        Connection connection = dataSource.getConnection();
        
        if(haveProduct(product.getName())){
        	return;
        }
        
        try {
            PreparedStatement pstmt = connection
                    .prepareStatement("INSERT INTO PRODUCTS (ID, NAME) VALUES (?, ?)");
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, product.getName());
            pstmt.executeUpdate();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    
    /**
     * Add a product to the table.	NEW UNDER CONSTRUCTION
     */
    public ArrayList<String> addProductNEW(String[] list) throws SQLException {
    	Connection connection = dataSource.getConnection();
    	PreparedStatement pstmt;
        ArrayList<String> idS = new ArrayList<String>();
        		
    	try {
	        for(String s: list){
	        	if(haveProduct(s)){
		        	pstmt = connection.prepareStatement("SELECT PRODUCTS.ID FROM PRODUCTS WHERE PRODUCTS.NAME = ?");
		        	pstmt.setString(1, s);
		        	ResultSet rs = pstmt.executeQuery();
		        	while (rs.next()) {
		                idS.add(rs.getString("ID"));
		            }
	        	}
	        	else {
	 
	                pstmt = connection
	                        .prepareStatement("INSERT INTO PRODUCTS (ID, NAME) VALUES (?, ?)");
	                String id=UUID.randomUUID().toString();
	                pstmt.setString(1, id);
	                pstmt.setString(2, s);		//direktno vliza v bazata - BADDDDDDD
	                pstmt.executeUpdate();
	                idS.add(id);
	                    
	            }
        	}
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
		return idS;
    }
    /**
     * Check if Products table already exists and create it if not.
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
     * Check if Products table already exists.
     */
    private boolean existsTable(Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getTables(null, null, "PRODUCTS", null);
        while (rs.next()) {
            String name = rs.getString("TABLE_NAME");
            if (name.equals("PRODUCTS")) {
                return true;
            }
        }
        return false;
    }
    /**
     * Create Products table.
     */
    private void createTable(Connection connection) throws SQLException {
        PreparedStatement pstmt = connection
                .prepareStatement("CREATE TABLE PRODUCTS "
                        + "(ID VARCHAR(255) PRIMARY KEY, "
                        + "NAME VARCHAR(255) UNIQUE NOT NULL)");
        pstmt.executeUpdate();
    }
}