package util;

import java.rmi.RemoteException;
import java.util.HashMap;
import core.ApplicationUser;
import core.TokenGenerator;
import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceProxy;

public class UserManager {
	
	/**
	 * The userList contains key-value pairs of bank account IDs to their cached application users.
	 * We use this when creating and deleting users to/from the system.
	 */
	public static HashMap<String, ApplicationUser> userList = new HashMap<String, ApplicationUser>();
	/**
	 * The barcodeList contains key-value pairs of bank account IDs to an array of available barcode tokens
	 * for the given user.
	 * When barcodes have been used during the transaction process, the value is set to null in the array.
	 */
	public static HashMap<String, String[]> barcodeList = new HashMap<>();
	/**
	 * The tokenGen is a factory for generating new Base64-encoded UUIDs. We use this for barcode token generation.
	 */
	private static TokenGenerator tokenGen = new TokenGenerator();
	/**
	 * The bank object is an interface using the SOAP protocol to communicate with the FastMoney bank.
	 */
	private static BankService bank = new BankServiceProxy();
	
	/**
	 * A configurable setting for the amount of barcodes a given user can have.
	 * Our initial limit is 10, meaning that after 10 transactions without a call to the POST /barcode
	 * resource will deplete the user of barcodes.
	 */
	public static final int BARCODE_LIMIT = 10;
	
	/**
	 * Adds an user to the system storage, which we can then check up on later.
	 * @param firstName The first name of the user.
	 * @param lastName The last name of the user.
	 * @param cprNumber The CPR number of the user. Make sure this is unique. ^___^
	 * @param token The bank account ID of the user in the FastMoney bank.
	 * @return
	 */
	public static ApplicationUser addUser(String firstName, String lastName, String cprNumber, String token) {
		ApplicationUser user = new ApplicationUser(firstName, lastName, cprNumber, token); // convert the arguments to our user class
		userList.put(token, user); // store the user by their bank account ID
		return user; // return the user, so our other code can make use of it. how nice!
	}
	
	/**
	 * Finds all the used barcode tokens for the user and refills those spots.
	 * In case there are not yet stored any barcode tokens for the user, we generate enough barcodes to fill the capacity
	 * In case all barcodes are not yet used, no changes are made
	 * @param token The bank account ID of the user to refill the barcode tokens for
	 * @return the list of barcode tokens for the user
	 */
	public static String[] generateAndReturnBarcodes(String token) {
		if (!userList.containsKey(token)) { // in case we don't know the user yet
			try {
				Account acc = bank.getAccount(token); // we try find the user. this can fail
				addUser(acc.getUser().getFirstName(), acc.getUser().getLastName(), acc.getUser().getCprNumber(), token); // if found, add the user
			} catch (RemoteException e) { // in case no user was found, we end with an exception, which we ignore - it's not relevant here
			}
			if (!userList.containsKey(token)) // if the user is still not found, we got the exception explained above
				return null; // we return null, so that our other code can handle the error reporting. low-level programming habits!
		}
		if (barcodeList.containsKey(token)) { // if we already have some barcode tokens stored for the user
			String[] userBarcodeTokens = barcodeList.get(token); // we get the array of barcode tokens for the user
			for (int i = 0; i < BARCODE_LIMIT; i++) // run through all entries of barcode tokens
				if (userBarcodeTokens[i] == null) // if a barcode token is used
					userBarcodeTokens[i] = tokenGen.generateBase64HexString(); // generate a new barcode token in the array!
			return userBarcodeTokens; // return the updated array of barcode tokens
		} else { // no barcodes stored for the user
			String[] userBarcodeTokens = new String[BARCODE_LIMIT]; // create a new empty array used to store barcode tokens
			for (int i = 0; i < BARCODE_LIMIT; i++) // run through all entries of barcode tokens
				userBarcodeTokens[i] = tokenGen.generateBase64HexString(); // generate a new barcode token in the array
			barcodeList.put(token, userBarcodeTokens); // store the array of barcode tokens using the user's bank account ID as the key
			return userBarcodeTokens; // return the array of barcode tokens
		}
	}
	
	/**
	 * Checks if there exists an account which has the given barcode token stored
	 * @param barcodeToken The barcode token to look for
	 * @return The bank account ID of the user which has this barcode token
	 */
	public static String getAccountIdFromBarcode(String barcodeToken) {
		for (String userId : barcodeList.keySet()) // run through all users who have barcode tokens stored
			for (int i = 0; i < BARCODE_LIMIT; i++) // run through all of the user's barcode tokens
				if (barcodeList.get(userId)[i] != null && barcodeList.get(userId)[i].equals(barcodeToken)) // if the barcode is not yet used and matches our search
					return userId; // return the bank account ID of the found user
		return null; // return null so that other code can handle the invalid barcode token
	}
	
	/**
	 * Removes a barcode token from our storage. Used when a transaction process has completed.
	 * We're using the bank account ID as well, because it makes for small performance gain compared to the getAccountIdFromBarcode.
	 * @param userId The bank account ID of the user with the barcode token
	 * @param barcodeToken The barcode token
	 */
	public static void removeBarcode(String userId, String barcodeToken) {
		if (barcodeList.containsKey(userId)) // check that the user has any barcodes stored
			for (int i = 0; i < BARCODE_LIMIT; i++) // run through all entries of barcode tokens
				if (barcodeList.get(userId)[i] != null && barcodeList.get(userId)[i].equals(barcodeToken)) // if the barcode is not yet used and matches our search
					barcodeList.get(userId)[i] = null; // remove the barcode token. >> return;
	}

}