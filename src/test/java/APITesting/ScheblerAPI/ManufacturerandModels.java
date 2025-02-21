package APITesting.ScheblerAPI;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.qameta.allure.Feature;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ManufacturerandModels {

	String token_Auth;
	String user_ID;
	
	@Feature("Manufacturer and Models API")
	@Test(priority = 1)
	public void userLogin() {
		
		// Test 
		
		HashMap<String,String> hm = new HashMap<String,String>();
		hm.put("username", "admin");
		hm.put("password", "admin@123");
		
		Response res = given()
							.filter(new AllureRestAssured())
							.contentType(ContentType.JSON)
							.body(hm)
						.when()
							.post("http://13.127.228.23/backend/index.php/login_check");
		 						
		Assert.assertEquals(res.getStatusCode(), 200);
		user_ID = res.jsonPath().get("user_id").toString();
		token_Auth = res.jsonPath().get("token").toString();
	}
	
	@Test(priority = 2, dependsOnMethods = {"userLogin"})
	public void manufaturerAndModelsList() {
		
		HashMap<String,String> head = new HashMap<String,String>();;
		head.put("Authorization", token_Auth);
		
		Response res = given()
							.filter(new AllureRestAssured())
							.headers(head)
						.when()
							.get("http://13.127.228.23/backend/index.php/get_manufacturers_models");
		
		String resMessage = res.jsonPath().get("response").toString();
		Assert.assertEquals(resMessage, "success");
		Assert.assertEquals(res.statusCode(), 200);
		
		
	}
	
	@Test(priority = 3, dependsOnMethods = {"manufaturerAndModelsList"})
	public void specificModelsList() {
		
		HashMap<String,String> head = new HashMap<String,String>();;
		head.put("Authorization", token_Auth);
		
		Response res = given()
							.filter(new AllureRestAssured())
							.headers(head)
						.when()
							.get("http://13.127.228.23/backend/index.php/get_model_objects_data/399");
		
		String resMessage = res.jsonPath().get("response").toString();
		Assert.assertEquals(resMessage, "success");
		Assert.assertEquals(res.statusCode(), 200);
		
		
	}
}
