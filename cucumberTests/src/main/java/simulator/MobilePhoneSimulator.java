package main.java.simulator;

import java.io.IOException;
import java.math.BigDecimal;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import main.java.core.ApplicationUser;
import main.java.dtu.ws.fastmoney.Account;
import main.java.dtu.ws.fastmoney.User;

/**
 * @author malthe
 * The simulator of the standalone MobileSimulator
 */
public class MobilePhoneSimulator {
	//public static String endpoint = "http://localhost:8080/DTUPayExample/rest";
	{
		Unirest.setObjectMapper(new ObjectMapper() {
			private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

			public <T> T readValue(String value, Class<T> valueType) {
				try {
					return jacksonObjectMapper.readValue(value, valueType);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			public String writeValue(Object value) {
				try {
					return jacksonObjectMapper.writeValueAsString(value);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
	
	
	
	/**
	 * This method creates a user trough the webApi of DTU pay by sending a post request
	 * @param user
	 * @return statuscode of the request
	 * @throws UnirestException
	 */
	public HttpResponse<String> createBarCode(ApplicationUser user) throws UnirestException {
		return Unirest.post("http://159.89.18.95:8080/burgerService/rest/barcode").
				header("Content-Type", "text/plain").body(user.getToken()).asString();
	}
	
	/**
	 * This method creates a user trough the webApi of DTU pay by send a get request.
	 * @param user
	 * @return statuscode of the request
	 * @throws UnirestException
	 */
	public int createAccount(ApplicationUser user) throws UnirestException {
		HttpResponse<String> r = Unirest.post("http://159.89.18.95:8080/burgerService/rest/users").
				header("Content-Type", "application/json").body(user).asString();
		return r.getStatus();
	}
	/**
	 * This method checks if a user has an account on DTU pay.
	 * @param user
	 * @return true if the server returns code 200, false otherwise
	 * @throws UnirestException
	 */
	public boolean accountExists(ApplicationUser user) throws UnirestException {
		HttpResponse<String> r = Unirest.get("http://159.89.18.95:8080/burgerService/rest/users/"+user.getToken()).asString();
		
		return r.getStatus() == 200;
	}
	
	/**
	 * Sends a get request to DTU-PAY to fetch the users current balance
	 * @param user
	 * @return the balance of the specified user
	 * @throws UnirestException
	 */
	public BigDecimal getAccountBalance(ApplicationUser user) throws UnirestException {
		HttpResponse<String> r = Unirest.get("http://159.89.18.95:8080/burgerService/rest/users/"+user.getToken()).asString();
		return new JSONObject(r.getBody()).getBigDecimal("balance");
	}
	
	/**
	 * contacts the DTU-PAY to check if the user has completed any transactions
	 * @param user
	 * @return true if the user has any transactions, false otherwise
	 * @throws UnirestException
	 */
	public boolean getTransactions(ApplicationUser user) throws UnirestException {
		HttpResponse<String> r = Unirest.get("http://159.89.18.95:8080/burgerService/rest/transaction/"+user.getToken()).asString();
		
		return r.getStatus() == 200;
	}
	
	/**
	 * Deletes the user with the specified uuid, by sending a delete request to the DTU-PAY web API
	 * @param uuid
	 * @return status code of the request
	 * @throws UnirestException
	 */
	public int deleteUserAccount(String uuid) throws UnirestException {
		HttpResponse<String> r = Unirest.delete("http://159.89.18.95:8080/burgerService/rest/users/"+uuid).asString();
		return r.getStatus();
	}
	
	/**
	 * Deletes the DTU-PAY account for the specified cpr number by contacting the DTU-PAY web API
	 * @param cpr
	 */
	public void deleteUserAccountFromCPR(String cpr) {
		try {
			HttpResponse<String> r = Unirest.get("http://159.89.18.95:8080/burgerService/rest/users/"+cpr).asString();
			if (r.getStatus() == 200) {
				JsonParser json = new JsonParser();
				JsonElement existingUser = json.parse(r.getBody());
				String accountId = existingUser.getAsJsonObject().get("id").getAsString();
				Unirest.delete("http://159.89.18.95:8080/burgerService/rest/users/" + accountId).asString().getBody();
			}
		} catch (UnirestException e) {
			System.out.println("DeleteUserAccountFromCPR error: " + e.getMessage());
		}
	}
	
	
	/**
	 * converts and returns an AppplicationUser, given an Account
	 * @param acc
	 * @return
	 */
	public ApplicationUser AccountToUser(Account acc) {
		return new ApplicationUser(
				acc.getUser().getFirstName(),
				acc.getUser().getLastName(),
				acc.getUser().getCprNumber(),
				acc.getId()
		);
	}
	
	/**
	 * converts an applicationUser to an Account and returns it
	 * @param user
	 * @return
	 */
	public Account UserToAccount(ApplicationUser user) {
		return new Account(new BigDecimal(0), user.getToken(), null, new User(
				user.getCprNumber(), user.getGivenName(), user.getSurname()
		));
	}
	/*public String randomCPR() {
		long timeSeed = System.nanoTime(); // to get the current date time value

        double randSeed = Math.random() * 1000; // random number generation

        long midSeed = (long) (timeSeed * randSeed); // mixing up the time and
        String s = midSeed + "";
        String subStr = s.substring(0, 9);
    	return subStr;
	}*/

}
