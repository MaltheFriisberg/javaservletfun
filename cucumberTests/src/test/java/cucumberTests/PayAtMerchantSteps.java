package test.java.cucumberTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import main.java.core.ApplicationUser;
import main.java.dtu.ws.fastmoney.Account;
import main.java.simulator.MerchantSimulator;
import main.java.simulator.MobilePhoneSimulator;

public class PayAtMerchantSteps {
	
	MerchantSimulator merchant = new MerchantSimulator();
	MobilePhoneSimulator simulator = new MobilePhoneSimulator();
	private int statusCode = 0;
	ApplicationUser user;
	ApplicationUser merchantUser;
	JSONArray barcodes;
	private double userBalanceCache;
	private double merchantBalanceCache;
	
	public ApplicationUser setup(String name, String cpr, boolean isMerchant) throws Throwable {
		Gson gson = new Gson();
		System.out.println("setup 1");
		ApplicationUser tmpUser = new ApplicationUser(name, "surname", cpr, null);
		System.out.println("setup 2");
		simulator.deleteUserAccountFromCPR(cpr);
		System.out.println("setup 3");
		JSONObject json = new JSONObject();
		json.put("givenName", name);
		json.put("surname", "surname");
		json.put("cprNumber", cpr);
		Unirest.post("http://159.89.18.95:8080/burgerService/rest/users").
				header("Content-Type", "application/json").body(json).asString();
		System.out.println("setup 4");
		HttpResponse<String> r = Unirest.get("http://159.89.18.95:8080/burgerService/rest/users/" + tmpUser.getCprNumber()).
				asString();
		System.out.println("setup 5");
		Account tmpAcc = gson.fromJson(r.getBody(), Account.class);
		if (isMerchant)
			merchantBalanceCache = tmpAcc.getBalance().doubleValue();
		else
			userBalanceCache = tmpAcc.getBalance().doubleValue();
		System.out.println("setup 6");
		return simulator.AccountToUser(tmpAcc);
	}
	
	@Given("^the customer named \"([^\"]*)\" with CPR \"([^\"]*)\" has an account$")
	public void the_customer_named_with_CPR_has_an_account(String arg1, String arg2) throws Throwable {
	    ApplicationUser temp = new ApplicationUser(arg1, null, arg2, null);
	    HttpResponse<String> r = Unirest.post("http://159.89.18.95:8080/burgerService/rest/users").
				header("Content-Type", "application/json").body(user).asString();
		
        Gson gson = new Gson();
		ApplicationUser temp1;
		this.user = gson.fromJson(r.getBody(), ApplicationUser.class);
		temp1 = gson.fromJson(r.getBody(), ApplicationUser.class);
		int test11 = 1;
		
	}

	@Given("^the customer named \"([^\"]*)\" with CPR \"([^\"]*)\" has a barcode$")
	public void the_customer_named_with_CPR_has_a_barcode(String arg1, String arg2) throws Throwable {
		HttpResponse<String> r = Unirest.post("http://159.89.18.95:8080/burgerService/rest/barcode").
				header("Content-Type", "application/json").body(this.user.getToken()).asString();
		
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
		this.barcodes = gson.fromJson(r.getBody(), type);
		assertTrue(barcodes.length() > 0);
	}

	@Given("^the merchant with ID (\\d+) exists$")
	public void the_merchant_with_ID_exists(int arg1) throws Throwable {
		
		ApplicationUser temp = new ApplicationUser("TestMerchant", null, "0703041212", null);
	    HttpResponse<String> r = Unirest.post("http://159.89.18.95:8080/burgerService/rest/users").
				header("Content-Type", "application/json").body(user).asString();
		
        Gson gson = new Gson();
		
		this.user = gson.fromJson(r.getBody(), ApplicationUser.class);
	    
		
	}

	@Given("^the customer has a balance of atleast (\\d+)$")
	public void the_customer_has_a_balance_of_atleast(int arg1) throws Throwable {
		HttpResponse<String> r = Unirest.get("http://159.89.18.95:8080/burgerService/rest/users/"+user.getToken()).asString();
		
		Gson gson = new Gson();
		
		Account accountInfo = gson.fromJson(r.getBody(), Account.class);
		
		//assertTrue(" (" + accountInfo.getBalance() + ") should be greater than current (" + arg1 + ")", accountInfo.getBalance() >= BigDecimal.valueOf(arg1).movePointLeft(2));
		assertTrue(accountInfo.getBalance().intValueExact()>= arg1);
	}
	
	@Given("^a barcode exists for the user with name \"([^\"]*)\" and CPR \"([^\"]*)\" with default balance$")
	public void a_barcode_exists_for_the_user_with_name_and_CPR_with_default_balance(String arg1, String arg2) throws Throwable {
		System.out.println("Default user 1");
		user = setup(arg1, arg2, false);
		System.out.println("Default user 2");
		barcodes = new JSONArray(simulator.createBarCode(user).getBody());
	}
	
	@Given("^a merchant exists and we know their bank account id$")
	public void a_merchant_exists_and_we_know_their_bank_account_id() throws Throwable {
		System.out.println("Merchant 1");
		merchantUser = setup("TestMerchant", "4644442236", true);
		System.out.println("Merchant 2");
	}

	@When("^the merchant makes a payment for the amount of (\\d+) and description \"([^\"]*)\" to the customer$")
	public void the_merchant_makes_a_payment_for_the_amount_of_and_description_to_the_customer(int arg1, String arg2) throws Throwable {
		this.statusCode = this.merchant.createTransaction(this.merchantUser.getToken(), this.barcodes.getString(0), arg1, arg2);
	}
	
	@Then("^the customer has (\\d+) less money on their balance$")
	public void the_customer_has_less_money_on_their_balance(int arg1) throws UnirestException {
		double balance = simulator.getAccountBalance(user).doubleValue();
		assertEquals(userBalanceCache - arg1, balance, 0.01);
	}
	
	@Then("^the merchant has (\\d+) more money on their balance$")
	public void the_merchant_has_more_money_on_their_balance(int arg1) throws UnirestException {
		double balance = simulator.getAccountBalance(merchantUser).doubleValue();
		assertEquals(userBalanceCache + arg1, balance, 0.01);
	}

	@Then("^i see the statuscode (\\d+)$")
	public void i_see_the_statuscode(int arg1) throws Throwable {
	    assertEquals(statusCode, 200);
	    this.simulator.deleteUserAccount(user.getToken());
	}

	@Given("^the customer has a balance of (\\d+)$")
	public void the_customer_has_a_balance_of(int arg1) throws Throwable {
		ApplicationUser temp = new ApplicationUser("jens", null, "1223121211", null);
	    HttpResponse<String> r = Unirest.post("http://159.89.18.95:8080/burgerService/rest/users").
				header("Content-Type", "application/json").body(user).asString();
		
        Gson gson = new Gson();
		
		this.user = gson.fromJson(r.getBody(), ApplicationUser.class);
		HttpResponse<String> r1 = Unirest.get("http://159.89.18.95:8080/burgerService/rest/users/"+user.getToken()).asString();
		
		Gson gson1 = new Gson();
		
		Account accountInfo = gson1.fromJson(r1.getBody(), Account.class);
		
	    assertEquals(accountInfo.getBalance().intValueExact(), 1000);
		
	}
	
	@Then("^i see the statuscode (\\d+) meaning failed transaction$")
	public void i_see_the_statuscode_meaning_failed_transaction(int arg1) throws Throwable {
		assertEquals(this.statusCode, arg1);
	}
	
	/*@After
	public void deleteUser() throws UnirestException {
		this.mobileSimulator.deleteUserAccount(this.user.getToken());
	}*/
	
}
	
	
	