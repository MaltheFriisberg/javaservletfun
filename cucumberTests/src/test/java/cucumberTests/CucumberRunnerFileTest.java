package test.java.cucumberTests;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = {
        "features/create_account.feature",
        "features/obtain_barcode.feature",
        "features/pay_at_merchant.feature"
},plugin =  {"pretty","html:target/html", "json:target/cucumber.json"},
glue = "test.java.cucumberTests",
strict = true
)
public class CucumberRunnerFileTest {
	

}

/*features = {
		//"src/test/java/cucumber/CucumberTest.feature",
		"features/create_account.feature",
		"features/obtain_barcode.feature"
}*/

/*features = {

		"features/create_account.feature",
		"features/obtain_barcode.feature",
		"features/pay_at_merchant.feature",

		}*/

