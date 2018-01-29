package main.java.core;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author malthe
 * The representation of Transactions in DTU pay
 */
@SuppressWarnings("serial")
public class Transaction implements Serializable {
	
	private String token;
	private boolean isMerchant;
	private BigDecimal amount;
	private BigDecimal balance;
	private String otherId;
	private String description;
	private long time;
	
	public Transaction() {
	}
	
	public Transaction(String token, boolean isMerchant, BigDecimal amount, BigDecimal balance, String otherId,
			String description, long time) {
		this.token = token;
		this.isMerchant = isMerchant;
		this.amount = amount;
		this.balance = balance;
		this.otherId = otherId;
		this.description = description;
		this.time = time;
	}

	public String getToken() {
		return token;
	}

	public boolean isMerchant() {
		return isMerchant;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public String getOtherId() {
		return otherId;
	}

	public String getDescription() {
		return description;
	}

	public long getTime() {
		return time;
	}
	
}
