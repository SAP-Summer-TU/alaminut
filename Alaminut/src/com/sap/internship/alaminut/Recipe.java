package com.sap.internship.alaminut;

import java.util.List;

/**
 * Class holding information on a Recipe.
 */

public class Recipe {

	private String id;
    private String name;
    private String howToMake;
    private List<String> products;
    
    public Recipe(String id, String name, String howToMake){
    	this.setId(id);
    	this.setName(name);
    	this.setHowToMake(howToMake);
    }
    
    public Recipe() {
		// TODO Auto-generated constructor stub
	}

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

    public String getHowToMake(){
    	return this.howToMake;
    }
    
    public void setHowToMake(String newHowToMake){
    	this.howToMake = newHowToMake;
    }
	
    public List<String> getProducts(){
    	return this.products;
    }
    
    public void setProducs(List<String> products){
    	this.products = products;
    }
    
}