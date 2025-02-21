package APITesting.ScheblerAPI;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.qameta.allure.Feature;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ProjectOperations {

	String token_Auth;
	String user_ID;
	String project_ID;
	String date;
	
	@Feature("Project operations API")
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
	public void addProject() {
		
		LocalDate str = java.time.LocalDate.now();
		date = str.toString();
		
		HashMap<String,String> hm = new HashMap<String,String>();
		hm.put("project_id", "");
		hm.put("project_date", date);
		hm.put("project_name", "Testing Automation API project");
		hm.put("project_no", "PO-01");
		hm.put("prepared_for", "Automation Testing");
		hm.put("with_of", "Eclipse and Rest Assured");
		hm.put("revision_id", "");
		hm.put("revision", "Revision-1");
		hm.put("user_id", user_ID);
		
		HashMap<String,String> head = new HashMap<String,String>();
		head.put("Authorization", token_Auth);
		
		Response res1 = given()
							.filter(new AllureRestAssured())
							.contentType(ContentType.JSON)
							.body(hm)
							.headers(head)
						.when()
							.post("http://13.127.228.23/backend/index.php/save_project");
		
		project_ID = res1.jsonPath().get("project_id").toString();
		String response1 = res1.jsonPath().get("response").toString();
		Assert.assertEquals(response1, "Project Created");
		Assert.assertEquals(res1.getStatusCode(), 201);
	}
	
	@Test(priority = 3, dependsOnMethods = {"addProject"})
	public void editProject() {
		
		HashMap<String,String> hm = new HashMap<String,String>();
		hm.put("project_id", project_ID);
		hm.put("project_date", date);
		hm.put("project_name", "Testing Automation API project updated");
		hm.put("project_no", "PO-01");
		hm.put("prepared_for", "Automation Testing updated");
		hm.put("with_of", "Eclipse and Rest Assured");
		hm.put("revision_id", "");
		hm.put("revision", "Revision-1");
		hm.put("user_id", user_ID);
		
		HashMap<String,String> head = new HashMap<String,String>();;
		head.put("Authorization", token_Auth);
		
		Response res2 = given()
							.filter(new AllureRestAssured())
							.contentType(ContentType.JSON)
							.body(hm)
							.headers(head)
						.when()
							.put("http://13.127.228.23/backend/index.php/save_project");
		
		String response1 = res2.jsonPath().get("response").toString();
		Assert.assertEquals(response1, "Project Updated");
		Assert.assertEquals(res2.getStatusCode(), 202);
	}
	
	@Test(priority = 4, dependsOnMethods = {"editProject"})
	public void projectList() {
		
		HashMap<String,String> head = new HashMap<String,String>();;
		head.put("Authorization", token_Auth);
		
		Response res = given()
							.filter(new AllureRestAssured())
							.headers(head)
						.when()
							.get("http://13.127.228.23/backend/index.php/projects");
		
		String resMessage = res.jsonPath().get("response").toString();
		Assert.assertEquals(resMessage, "success");
		Assert.assertEquals(res.statusCode(), 200);
	}
	
	@Test(priority = 5, dependsOnMethods = {"editProject"})
	public void singleProjectDetails() {
		
		HashMap<String,String> head = new HashMap<String,String>();;
		head.put("Authorization", token_Auth);
		
		Response res = given()
							.filter(new AllureRestAssured())
							.headers(head)
						.when()
							.get("http://13.127.228.23/backend/index.php/projects/" + project_ID);
		
		String resMessage = res.jsonPath().get("response").toString();
		Assert.assertEquals(resMessage, "success");
		Assert.assertEquals(res.statusCode(), 200);
	}
	
	@Test(priority = 6, dependsOnMethods = {"editProject"})
	public void deleteProject() {
		
		HashMap<String,String> hm = new HashMap<String,String>();
		hm.put("project_id", project_ID);
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
							.delete("http://13.127.228.23/backend/index.php/remove_project");
		
		String response1 = res4.jsonPath().get("response").toString();
		Assert.assertEquals(response1, "Project Deleted");
		Assert.assertEquals(res4.getStatusCode(), 200);
	}
}
