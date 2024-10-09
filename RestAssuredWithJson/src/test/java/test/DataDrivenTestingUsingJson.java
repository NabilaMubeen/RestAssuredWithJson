package test;

import static org.testng.Assert.assertEquals;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import Utils.FileNameConstants;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;


public class DataDrivenTestingUsingJson {
  @Test(dataProvider = "getTestData")
  public void DataDrivenTesting(LinkedHashMap<String,String> testData) {
	  
	try {
		// Prepare the body using testData values
		    Map<String, Object> requestBody = new HashMap<>();
		    requestBody.put("id", testData.get("id"));
		    requestBody.put("firstname", testData.get("firstname"));
		    requestBody.put("lastname", testData.get("lastname"));
		    requestBody.put("email", testData.get("email"));
		  
		  // Convert Map to JSON string using Jackson ObjectMapper
		    ObjectMapper objectMapper = new ObjectMapper();
		    String jsonBody = objectMapper.writeValueAsString(requestBody);
		 	
		  Response response = RestAssured.given().log().all()
					.contentType(ContentType.JSON)
					.body(jsonBody).log().all()
					.baseUri("https://reqres.in/api/users")
					.when()
					.post()
					.then()
					.log().all()
					.extract()
					.response();
		  assertEquals(response.statusCode(), 201, "Expected status code is not matching!");
	} catch (JsonProcessingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
	  getuser(testData);
  }
  
  public void getuser(Map<String,String> testData) {
		String Userid = testData.get("id");
		  Response response = RestAssured.given()
				  .pathParam("id", Userid)
		            .when()
		            .get("https://reqres.in/api/users/{id}")
		            .then()
		            .statusCode(200) // Assert the status code is 200 for valid user ID
		            .extract().response();
		  System.out.println(response);
		  
		  if (Integer.parseInt(Userid) == 1) {
			  Deleteuser(Userid);
	      }
	  }
	  public void Deleteuser(String Userid) {

		  Response deleteResponse = RestAssured.given()
				  .pathParam("id", Userid)
		          .when()
		          .delete("https://reqres.in/api/users/{id}")
		          .then()
		          .statusCode(204) // Assert the delete operation was successful (204 No Content)
		          .extract().response();
		  System.out.println("User with ID: " + Userid + " has been deleted.");
		  
	  }
  
  @DataProvider(name = "getTestData")
  public Object [] getTestDataUsingJsonFile() {
	  
	  Object[] obj = null;
	try {
		String jsonTestData = FileUtils.readFileToString(new File(FileNameConstants.JSON_TEST_DATA),"UTF-8");
		JSONArray jsonArray = JsonPath.read(jsonTestData, "$");
		obj = new Object[jsonArray.size()];
		
		for(int i = 0; i < jsonArray.size(); i++) {
			
			obj[i] = jsonArray.get(i);
		}
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	   
	  return obj;
  }
}
