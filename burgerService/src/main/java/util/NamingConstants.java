package util;

/**
 * @author s153666
 * Contains the location for the queues in the JMS system & the connectionFactory location
 */
public class NamingConstants {
	// Queues --------------------
	// Account
	public static final String createAccountQueue = "java:/users/createAccountQueue";
	public static final String getAccountQueue = "java:/users/getAccountQueue";
	public static final String deleteAccountQueue = "java:/users/deleteAccountQueue";

	// Transaction
	public static final String createTransactionQueue = "java:/transactions/createTransactionQueue";
	public static final String getTransactionsQueue = "java:/transactions/getTransactionsQueue";
	
	// Barcode
	public static final String getBarCodesQueue = "java:/barcodes/getBarcodesQueue";
	
	// Connection Factory ----------
	public static final String connectionFactory = "java:/myJms/MyConnectionFactory";
}
