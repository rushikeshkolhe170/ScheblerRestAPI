package APITesting.ScheblerAPI;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.qameta.allure.Feature;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;

public class UserOperations {

	String token_Auth;
	String user_ID;
	String new_User_ID;
	String pass;
	int total_Users;
	
	@Feature("User Operations API")
	@Test(priority = 1)
	public void userLogin() {
		
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
	public void addUser() {
		
		HashMap<String,String> hm = new HashMap<String,String>();
		hm.put("user_id", "");
		hm.put("username", "TestingAutomationAPI6@schebler.com");
		hm.put("password", "welcome");
		hm.put("full_name", "AutomationAPITesting6");
		hm.put("phone_no", "9889988998");
		hm.put("role_id", "1");
		hm.put("added_by", user_ID);
		
		HashMap<String,String> head = new HashMap<String,String>();
		head.put("Authorization", token_Auth);
		
		Response res1 = given()
							.filter(new AllureRestAssured())
							.contentType(ContentType.JSON)
							.body(hm)
							.headers(head)
						.when()
							.post("http://13.127.228.23/backend/index.php/saveUser");
		
		new_User_ID = res1.jsonPath().get("user_id").toString();
		String response1 = res1.jsonPath().get("response").toString();
		Assert.assertEquals(response1, "User Added");
		Assert.assertEquals(res1.getStatusCode(), 201);
	}
	
	@Test(priority = 3, dependsOnMethods = {"addUser"})
	public void editUser() {
		
		HashMap<String,String> hm = new HashMap<String,String>();
		hm.put("user_id", new_User_ID);
		hm.put("username", "TestingAutomationAPIUpdate@schebler.com");
		hm.put("password", "welcome");
		hm.put("full_name", "AutomationAPITestingUpdate");
		hm.put("phone_no", "9889988998");
		hm.put("role_id", "1");
		hm.put("added_by", user_ID);
		
		HashMap<String,String> head = new HashMap<String,String>();
		head.put("Authorization", token_Auth);
		
		Response res2 = given()
							.filter(new AllureRestAssured())
							.contentType(ContentType.JSON)
							.body(hm)
							.headers(head)
						.when()
							.put("http://13.127.228.23/backend/index.php/saveUser");
		
		pass = hm.get("password").toString();
		String response1 = res2.jsonPath().get("response").toString();
		Assert.assertEquals(response1, "User Updated");
		Assert.assertEquals(res2.getStatusCode(), 202);
	}
	
	@Test(priority = 4, dependsOnMethods = {"editUser"})
	public void toatlUserCount() {
		
		HashMap<String,String> head = new HashMap<String,String>();
		head.put("Authorization", token_Auth);
		
		Response res = given()
							.filter(new AllureRestAssured())
							.contentType(ContentType.JSON)
							.headers(head)
						.when()
							.get("http://13.127.228.23/backend/index.php/dashboard");
		
		String response1 = res.jsonPath().get("response").toString();
		Assert.assertEquals(response1, "success");
		Assert.assertEquals(res.getStatusCode(), 200);
		String size = res.jsonPath().get("result.total_users").toString();
		total_Users = Integer.parseInt(size);
	}
	
	@Test(priority = 5, dependsOnMethods = {"editUser"})
	public void userStatus() {
		
		HashMap<String,String> hm = new HashMap<String,String>();
		hm.put("user_id", new_User_ID);
		hm.put("status", "0");
		hm.put("updated_by", user_ID);
		
		HashMap<String,String> head = new HashMap<String,String>();
		head.put("Authorization", token_Auth);
		
		Response res4 = given()
							.filter(new AllureRestAssured())
							.contentType(ContentType.JSON)
							.body(hm)
							.headers(head)
						.when()
							.put("http://13.127.228.23/backend/index.php/change_user_status");
		
		String response1 = res4.jsonPath().get("response").toString();
		Assert.assertEquals(response1, "User Status Updated");
		Assert.assertEquals(res4.getStatusCode(), 202);
	}
	
	@Test(priority = 6, dependsOnMethods = {"editUser"})
	public void changePassword() {
		
		HashMap<String,String> hm = new HashMap<String,String>();
		hm.put("user_id", new_User_ID);
		hm.put("old_password", pass);
		hm.put("password", "Test@123");
		
		HashMap<String,String> head = new HashMap<String,String>();
		head.put("Authorization", token_Auth);
		
		Response res3 = given()
							.filter(new AllureRestAssured())
							.contentType(ContentType.JSON)
							.body(hm)
							.headers(head)
						.when()
							.put("http://13.127.228.23/backend/index.php/change_password");
		
		String response1 = res3.jsonPath().get("response").toString();
		Assert.assertEquals(response1, "User Password Updated");
		Assert.assertEquals(res3.getStatusCode(), 202);
	}
	
	@Test(priority = 7, dependsOnMethods = {"changePassword"})
	public void availableUsersList() {
		
		HashMap<String,String> head = new HashMap<String,String>();;
		head.put("Authorization", token_Auth);
		
		Response res = given()
							.filter(new AllureRestAssured())
							.headers(head)
						.when()
							.get("http://13.127.228.23/backend/index.php/userList");
		
		String resMessage = res.jsonPath().get("response").toString();
		Assert.assertEquals(resMessage, "success");
		Assert.assertEquals(res.statusCode(), 200);
		JSONObject jo = new JSONObject(res.asString());
		int size = jo.getJSONArray("users").length();
		Assert.assertEquals(size, total_Users);
	}
	
	@Test(priority = 8, dependsOnMethods = {"changePassword"})
	public void deleteUser() {
		
		HashMap<String,String> hm = new HashMap<String,String>();
		hm.put("user_id", new_User_ID);
		hm.put("is_delete", "0");
		hm.put("deleted_by", user_ID);
		
		HashMap<String,String> head = new HashMap<String,String>();
		head.put("Authorization", token_Auth);
		
		Response res4 = given()
							.filter(new AllureRestAssured())
							.contentType(ContentType.JSON)
							.body(hm)
							.headers(head)
						.when()
							.delete("http://13.127.228.23/backend/index.php/remove_user");
		
		String response1 = res4.jsonPath().get("response").toString();
		Assert.assertEquals(response1, "User Deleted");
		Assert.assertEquals(res4.getStatusCode(), 200);
	}
}
