package test.java.cucumberTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import main.java.core.ApplicationUser;
import main.java.dtu.ws.fastmoney.Account;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.Reportable;
import main.java.simulator.MerchantSimulator;
import main.java.simulator.MobilePhoneSimulator;

public class CreateBarcodeSteps {
	
	MobilePhoneSimulator simulator = new MobilePhoneSimulator();
	MerchantSimulator merchant = new MerchantSimulator();
	private String name = "hans";
	private String cpr = "121212121217";
	ApplicationUser user;
	int statusCode = 0;
	private String barcodeCache;
	private String newBarcodeCache;
	private String usedBarcode;
	
	private boolean hasBeenTested = false; // used to prevent the After method.
	
	public void setup() throws Throwable {
		hasBeenTested = true;
		Gson gson = new Gson();
		user = new ApplicationUser(name, "surname", cpr, null);
		simulator.deleteUserAccountFromCPR(cpr);
		Unirest.post("http://159.89.18.95:8080/burgerService/rest/users").
				header("Content-Type", "application/json").body(user).asString();
		HttpResponse<String> r = Unirest.get("http://159.89.18.95:8080/burgerService/rest/users/" + user.getCprNumber()).
				asString();
		Account tmpAcc = gson.fromJson(r.getBody(), Account.class);
		user = simulator.AccountToUser(tmpAcc);
	}
	
	@Given("^a barcode does not exists for the user with name \"([^\"]*)\" and CPR \"([^\"]*)\"$")
	public void a_barcode_does_not_exists_for_the_user_with_name_and_CPR(String arg1, String arg2) throws Throwable {
		setup();
	}
	
	@Given("^a user with uuid \"([^\"]*)\" does not exists$")
	public void a_user_with_uuid_does_not_exists(String arg1) {
		try {
			simulator.deleteUserAccount(arg1);
		} catch (UnirestException e) {
		}
	}

	@When("^I create a barcode for the user$")
	public void i_create_a_barcode_for_the_user() throws Throwable {
		this.statusCode = simulator.createBarCode(this.user).getStatus();
	}
	
	@When("^I create a barcode for the user with uuid \"([^\"]*)\"$")
	public void i_create_a_barcode_for_the_user_with_uuid(String arg1) throws Throwable {
		ApplicationUser tempUser = new ApplicationUser("empty", "test", "334334335", arg1);
		this.statusCode = simulator.createBarCode(tempUser).getStatus();
	}

	@Then("^I see the status code (\\d+)$")
	public void i_see_the_status_code(int arg1) throws Throwable {
	    assertEquals(arg1, statusCode);
	}

	@Given("^the user has (\\d+) barcodes and tries to generate more$")
	public void the_user_has_barcodes_and_tries_to_generate_more(int arg1) throws Throwable {
		setup();
		barcodeCache = simulator.createBarCode(user).getBody();
	}

	@When("^i create a barcode for the user with name \"([^\"]*)\" and CPR \"([^\"]*)\"$")
	public void i_create_a_barcode_for_the_user_with_name_and_CPR(String arg1, String arg2) throws Throwable {
	    newBarcodeCache = simulator.createBarCode(user).getBody();
	}
	
	@Then("^I see that both results from generating new barcodes is the same$")
	public void i_see_that_both_results_from_generating_new_barcodes_is_the_same() {
		assertTrue(barcodeCache.equals(newBarcodeCache));
	}
	
	@Given("^the user has used one of their barcodes and noted it down$")
	public void the_user_has_used_one_of_their_barcodes_and_noted_it_down() throws Throwable {
		setup();
		String barcodeResp = simulator.createBarCode(user).getBody();
		JsonArray json = new JsonParser().parse(barcodeResp).getAsJsonArray();
		usedBarcode = json.get(0).getAsString();
		assertEquals(200, merchant.createTransaction(user.getToken(), usedBarcode, 50, "Cucumber test (0_0)"));
	}
	
	@When("^I try to make a transaction with the used barcode$")
	public void i_try_to_make_a_transaction_with_the_used_barcode() throws Throwable {
		statusCode = merchant.createTransaction(user.getToken(), usedBarcode, 50, "Cucumber test 2 (0_0)");
	}
	
	//Copied from https://github.com/damianszczepanik/cucumber-reporting
			@After
			public void after() throws Throwable{
				if (!hasBeenTested)
					return;
				
				//
				this.simulator.deleteUserAccount(this.user.getToken());
				
				File reportOutputDirectory = new File("target");
				List<String> jsonFiles = new ArrayList<String>();
				jsonFiles.add("cucumber-report-1.json");
				jsonFiles.add("cucumber-report-2.json");

				String buildNumber = "1";
				String projectName = "cucumberProject";
				boolean runWithJenkins = false;
				boolean parallelTesting = false;

				Configuration configuration = new Configuration(reportOutputDirectory, projectName);
				// optional configuration
				configuration.setParallelTesting(parallelTesting);
				configuration.setRunWithJenkins(runWithJenkins);
				configuration.setBuildNumber(buildNumber);
				// addidtional metadata presented on main page
				configuration.addClassifications("Platform", "Windows");
				configuration.addClassifications("Browser", "Firefox");
				configuration.addClassifications("Branch", "release/1.0");

				// optionally add metadata presented on main page via properties file
				List<String> classificationFiles = new ArrayList<String>();
				classificationFiles.add("properties-1.properties");
				classificationFiles.add("properties-2.properties");
				configuration.addClassificationFiles(classificationFiles);

				ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
				Reportable result = reportBuilder.generateReports();
				// and here validate 'result' to decide what to do
				// if report has failed features, undefined steps etc
			}
	
}
