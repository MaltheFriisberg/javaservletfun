package main.java.core;

import java.io.Serializable;

/**
 * @author malthe
 * The representation of the DTU pay user
 *
 */
public class ApplicationUser implements Serializable {
	
	private String givenName, surname, cprNumber, token;
    //private Sex sex;
	
	
	
	public ApplicationUser(String givenName, String surname, String cprNumber, String token) {
		super();
		this.givenName = givenName;
		this.surname = surname;
		this.cprNumber = cprNumber;
		this.token = token;
		//this.sex = sex;
	}

	

	public String getGivenName() {
		return givenName;
	}



	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}



	public String getSurname() {
		return surname;
	}



	public void setSurname(String surname) {
		this.surname = surname;
	}



	public String getCprNumber() {
		return cprNumber;
	}



	public void setCprNumber(String cprNumber) {
		this.cprNumber = cprNumber;
	}



	public String getToken() {
		return token;
	}



	public void setToken(String token) {
		this.token = token;
	}
	
	


}
