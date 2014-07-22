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
    public boolean haveProduct(Products product) throws SQLException {
        Connection connection = dataSource.getConnection();
        int id=0;
        try {
            PreparedStatement pstmt = connection
                    .prepareStatement("SELECT PRODUCTS.ID FROM PRODUCTS WHERE PRODUCTS.NAME = ?");
            pstmt.setString(1, product.getName());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                id=rs.getInt("ID");
            }
            if(id!=0) return true;
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
                        + "(ID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
                        + "NAME VARCHAR(255) UNIQUE NOT NULL,");
        pstmt.executeUpdate();
    }
}