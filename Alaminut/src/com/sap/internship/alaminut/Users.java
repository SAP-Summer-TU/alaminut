package com.sap.internship.alaminut;

/**
 * Class holding information on a Users.
 */

public class Users {

	private String id;
    private String name;
    private String password;
    private String email;

    public String getId() {
        return id;
    }

    public void setId(String newId) {
        this.id = newId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String getPassword(){
    	return this.password;
    }
    
    public void setPassword(String newPassword){
    	this.email = newPassword;
    }
    
    public String getEmail(){
    	return this.email;
    }
    
    public void setHowToMake(String newEmail){
    	this.email = newEmail;
    }
	
}