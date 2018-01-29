package core;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ApplicationUser implements Serializable {
	
	private String givenName, surname, cprNumber, token;
	
	/**
	 * No param constructor
	 */
	public ApplicationUser() {
		super();
	}
	
	/**
	 * 
	 * @param givenName
	 * @param surname
	 * @param cprNumber
	 * @param token
	 */
	public ApplicationUser(String givenName, String surname, String cprNumber, String token) {
		super();
		this.setGivenName(givenName);
		this.setSurname(surname);
		this.setCprNumber(cprNumber);
		this.setToken(token);
	}

	/**
	 * Returns the name of the user
	 * @return
	 */
	public String getGivenName() {
		return givenName;
	}

	/**
	 * Sets the given name
	 * @param givenName
	 */
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
