package com.sap.internship.alaminut;

/**
 * Class holding information on a Recipe.
 */

public class Recipe {

	private String id;
    private String name;
    private String howToMake;

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
	
}