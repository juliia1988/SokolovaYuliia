package api.tests;

import api.models.args.pet.*;
import api.steps.BaseApiStep;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UpdateExistingPet extends BaseApiStep {

    private int pet_id;

    @Test
    @Description("Check that Pet can be updated")

    public void testUpdatePetWithValidData() {

        // Create a pet and get an id

        PetId petId = new PetId();
        petId.createPetAndGetId();
        pet_id = petId.getPet_id();

        // Create new Pet object with new name
        AddPetStoreBuilder addPetStoreBuilder = new AddPetStoreBuilder();
        AddPetToStore pet = addPetStoreBuilder.buildPet("new kitty", pet_id);

        // Convert AddPetStore object to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(pet);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Perform PUT request to update a pet
        Response response = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .put("/pet");

        // Assertion
        response.then()
                .statusCode(200)
                .body("name", equalTo("new kitty"))
                .body("status", equalTo("available"));
        System.out.println("Pet was updated successfuly with id: " + response.path("id") + " and new name: " + response.path("name"));
    }

    @Test
    @Description("Check that Pet can be updated by Id")
    public void testUpdatePetById() {

        // Create a pet and get an id

        PetId petId = new PetId();
        petId.createPetAndGetId();
        pet_id = petId.getPet_id();

        // Create Pet object
        UpdatePetBuilder updatePetBuilder = new UpdatePetBuilder();
        UpdatePetById pet = updatePetBuilder.updatePet("new kitty", "available", pet_id);

        // Convert AddPetStore object to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(pet);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Perform POST request to update a pet
        Response response = given()
                .contentType("application/x-www-form-urlencoded")
                .body(requestBody)
                .when()
                .post("/pet/" + pet_id);

        // Assertion
        response.then()
                .statusCode(200);
        System.out.println("Pet with id " + response.path("message") + " updated successfuly");
    }

    // We could add more tests here to check validation of the data (update pet with empty name, update pet with empty id, etc.) But this test now failed because any validation was implemented ot this pet resourse.


    @Test
    @Description("Check that Pet can not be updated with empty name")
    public void testUpdatePetWithEmptyName() {

        // Create a pet and get an id

        PetId petId = new PetId();
        petId.createPetAndGetId();
        pet_id = petId.getPet_id();

        // Create new Pet object with empty name

        AddPetStoreBuilder addPetStoreBuilder = new AddPetStoreBuilder();
        AddPetToStore pet = addPetStoreBuilder.buildPet("", pet_id);

        // Convert AddPetStore object to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(pet);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Perform PUT request to update a pet
        Response response = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .put("/pet");

        // Assertion
        response.then()
                .statusCode(400);
        System.out.println("Pet was not updated because of validation error: " + response.path("message") + " and code: " + response.path("code"));
    }

}
