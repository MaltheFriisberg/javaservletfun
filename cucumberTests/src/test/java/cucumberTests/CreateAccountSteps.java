package test.java.cucumberTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import cucumber.api.java.Before;
import main.java.core.ApplicationUser;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import main.java.dtu.ws.fastmoney.*;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.Reportable;
import main.java.simulator.MobilePhoneSimulator;
//import org.junit.Before;


public class CreateAccountSteps {
	
	ApplicationUser user;
	MobilePhoneSimulator mobilePhone;
	
	String accountId;
	int status = 0;
	int status1 = 0;
	String cprToDelete;

	@Before
	public void before() {
		
	}

	@Given("^a user with name \"([^\"]*)\" and CPR number \"([^\"]*)\" who does not have a bank account$")
	public void aUserWithNameAndCPRNumberWhoDoesNotHaveABankAccount(String name, String cpr) throws Throwable {
		user = new ApplicationUser(name, null, cpr, null);
		
	}

	@Given("^a user with name \"([^\"]*)\" and CPR number \"([^\"]*)\" with bank account$")
	public void userWithNameAndCPRNumberWithBankAccount(String name, String cpr) throws Throwable {
		this.mobilePhone = new MobilePhoneSimulator();
		user = new ApplicationUser(name, "username", cpr, null);
		this.cprToDelete = cpr;
		this.mobilePhone.createAccount(user);
	}

	@Then("^I see the status (\\d+)$")
	public void iSeeTheStatus(int arg1) throws Throwable {
		assertEquals(arg1, status);
	}

	@Given("^user with name \"([^\"]*)\" and CPR number \"([^\"]*)\"$")
	public void userWithNameAndCPRNumber(String name, String cpr) throws Throwable {
		user = new ApplicationUser(name,"username", cpr, null);
	}

	@When("^I create an account for the user$")
	public void iCreateAnAccountForTheUser() throws Throwable {
		mobilePhone = new MobilePhoneSimulator();
		status = mobilePhone.createAccount(user);
	}

	@Then("^account creation is successful$")
	public void accountCreationIsSuccessful() throws Throwable {
		assertTrue(mobilePhone.accountExists(user));
	}

	
	//Copied from https://github.com/damianszczepanik/cucumber-reporting
		/*@After
		public void after() throws Throwable{
			this.mobilePhone.deleteUserAccountFromCPR(this.cprToDelete);
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
		}*/
	
}

