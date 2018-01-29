package main.java.simulator;

import java.io.IOException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MerchantSimulator {
	
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
	 * @param merchantAccountId The Bank-ID for the merchant account
	 * @param barcodeToken The barcode token the user wish to use to pay
	 * @param price The ammount of money getting transferred
	 * @param description The message sent with the transaction
	 * @return The status code for the request
	 * @throws UnirestException
	 */
	public int createTransaction(String merchantAccountId, String barcodeToken, int price, String description) throws UnirestException {
		JSONObject json = new JSONObject();
		json.put("token", barcodeToken);
		json.put("otherId", merchantAccountId);
		json.put("amount", price);
		json.put("description", description);
		HttpResponse<String> r = Unirest.post("http://159.89.18.95:8080/burgerService/rest/transaction").
				header("Content-Type", "application/json").body(json.toString()).asString();
		return r.getStatus();
	}
	
	

}
