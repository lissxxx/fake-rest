package io.github.ivanrosw.fakerest.core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import io.github.ivanrosw.fakerest.core.utils.RestClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {"spring.config.location = classpath:application.yml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = FakeRestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class FakeRestApplicationTests {

	private static final String ID_PARAM = "id";
	private static final String BAD_ID = "bad_id";
	private static final String BAD_DATA = "bad data";

	private static final String TEST_CONTROLLER_URI = "/test/";
	private static final String TEST_ROUTER_URI = "/test";
	private static final String TEST_CONTROLLER2_URI = "/test2";
	private static final String TEST_CONTROLLER3_URI_MODIFY = "/test3/";
	private static final String TEST_CONTROLLER3_URI = "/test3";
	private static final String TEST_CONTROLLER4_URI = "/test4";
	private static final String TEST_CONTROLLER5_URI = "/test5";

	@LocalServerPort
	private int port;

	private String baseUrl;

	@Autowired
	private RestClient restClient;
	@Autowired
	private JsonUtils jsonUtils;

	@Test
	void integrationTest() throws Exception{
		baseUrl = "http://localhost:" + port;

		testCollectionControllersAndRouters();
		testStaticControllers();
		testDelay();
		testRouterTimeout();
	}

	private void testCollectionControllersAndRouters() throws Exception{
		ObjectNode expectedJson = jsonUtils.createJson();
		jsonUtils.putString(expectedJson, "data", "value");
		//GET ALL EMPTY
		String emptyArray = "[]";
		assertThat(getAll(TEST_CONTROLLER_URI)).isEqualTo(emptyArray);
		//GET ALL ROUTER EMPTY
		assertThat(getAll(TEST_ROUTER_URI)).isEqualTo(emptyArray);

		//GET ONE NOT FOUND
		assertThat(restClient.execute(HttpMethod.GET, new URI(baseUrl + TEST_CONTROLLER_URI + "/" + BAD_ID), null, null).getStatusCode())
				.isEqualTo(HttpStatus.NOT_FOUND);

		//CREATE WITH GENERATE ID
		createOne(TEST_CONTROLLER_URI, expectedJson);
		assertThat(getOne(TEST_CONTROLLER_URI, jsonUtils.getString(expectedJson, ID_PARAM))).isEqualTo(expectedJson.toString());

		//CREATE NULL BODY
		assertThat(restClient.execute(HttpMethod.POST, new URI(baseUrl + TEST_CONTROLLER_URI), null, null).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);

		//CREATE BAD BODY
		assertThat(restClient.execute(HttpMethod.POST, new URI(baseUrl + TEST_CONTROLLER_URI), null, BAD_DATA).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);

		//GET ALL CREATED
		assertThat(getAll(TEST_CONTROLLER_URI)).isEqualTo("[" + expectedJson + "]");
		//GET ALL CREATED ROUTER
		assertThat(getAll(TEST_ROUTER_URI)).isEqualTo("[" + expectedJson + "]");

		//UPDATE
		jsonUtils.putString(expectedJson, "data", "new value");
		update(TEST_CONTROLLER_URI, jsonUtils.getString(expectedJson, ID_PARAM), expectedJson);

		//UPDATE NULL BODY
		assertThat(restClient.execute(HttpMethod.PUT, new URI(baseUrl +TEST_CONTROLLER_URI + "/" + jsonUtils.getString(expectedJson, ID_PARAM)), null, null).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);

		//UPDATE BAD BODY
		assertThat(restClient.execute(HttpMethod.PUT, new URI(baseUrl + TEST_CONTROLLER_URI + "/" + jsonUtils.getString(expectedJson, ID_PARAM)), null, BAD_DATA).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);

		//UPDATE BAD ID
		assertThat(restClient.execute(HttpMethod.PUT, new URI(baseUrl + TEST_CONTROLLER_URI + "/" + BAD_ID), null, expectedJson.toString()).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);

		//GET ALL UPDATED
		assertThat(getAll(TEST_CONTROLLER_URI)).isEqualTo("[" + expectedJson + "]");
		//GET ALL UPDATED ROUTER
		assertThat(getAll(TEST_ROUTER_URI)).isEqualTo("[" + expectedJson + "]");

		//DELETE
		delete(TEST_CONTROLLER_URI, jsonUtils.getString(expectedJson, ID_PARAM));
		assertThat(getAll(TEST_CONTROLLER_URI)).isEqualTo(emptyArray);
		assertThat(getAll(TEST_ROUTER_URI)).isEqualTo(emptyArray);

		//DELETE BAD ID
		assertThat(restClient.execute(HttpMethod.DELETE, new URI(baseUrl + TEST_CONTROLLER_URI + "/" + BAD_ID), null, null).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);

		//CREATE ROUTER WITH GENERATE ID
		createOne(TEST_ROUTER_URI, expectedJson);
		assertThat(getOne(TEST_ROUTER_URI, jsonUtils.getString(expectedJson, ID_PARAM))).isEqualTo(expectedJson.toString());

		//CREATE WITHOUT GENERATE ID OK
		jsonUtils.putString(expectedJson, ID_PARAM, "1");
		createOne(TEST_CONTROLLER3_URI_MODIFY, expectedJson);
		assertThat(jsonUtils.getString(expectedJson, ID_PARAM)).isEqualTo("1");

		//CREATE WITHOUT ID ALREADY EXIST
		assertThat(restClient.execute(HttpMethod.POST, new URI(baseUrl + TEST_CONTROLLER3_URI_MODIFY), null, expectedJson.toString()).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	private String getAll(String uri) throws Exception {
		return restClient.execute(HttpMethod.GET, new URI(baseUrl + uri), null, null).getBody();
	}

	private String getOne(String uri, String id) throws Exception {
		return restClient.execute(HttpMethod.GET, new URI(baseUrl + uri + "/" + id), null, null).getBody();
	}

	private void createOne(String uri, ObjectNode body) throws Exception {
		String result = restClient.execute(HttpMethod.POST, new URI(baseUrl + uri), null, body.toString()).getBody();
		ObjectNode resultJson = jsonUtils.toObjectNode(result);
		jsonUtils.putString(body, ID_PARAM, jsonUtils.getString(resultJson, ID_PARAM));
	}

	private void update(String uri, String id, ObjectNode body) throws Exception {
		String result = restClient.execute(HttpMethod.PUT, new URI(baseUrl + uri + "/" + id), null, body.toString()).getBody();
		ObjectNode resultJson = jsonUtils.toObjectNode(result);
		jsonUtils.putString(body, ID_PARAM, jsonUtils.getString(resultJson, ID_PARAM));
	}

	private void delete(String uri, String id) throws Exception {
		restClient.execute(HttpMethod.DELETE, new URI(baseUrl + uri + "/" + id), null, null);
	}

	private void testStaticControllers() throws Exception {
		String expectedAnswer = "expected answer";
		String expectedAnswer2 = "expected answer2";

		//GET WITHOUT ANSWER
		String actualAnswer = restClient.execute(HttpMethod.GET, new URI(baseUrl + TEST_CONTROLLER2_URI), null, null).getBody();
		assertThat(actualAnswer).isNull();

		//GET WITH ANSWER
		actualAnswer = restClient.execute(HttpMethod.GET, new URI(baseUrl + TEST_CONTROLLER3_URI), null, null).getBody();
		assertThat(actualAnswer).isEqualTo(expectedAnswer);

		//POST WITHOUT ANSWER
		assertThat(restClient.execute(HttpMethod.POST, new URI(baseUrl + TEST_CONTROLLER2_URI), null, expectedAnswer).getBody()).isEqualTo(expectedAnswer);

		//POST WITH ANSWER
		assertThat(restClient.execute(HttpMethod.POST, new URI(baseUrl + TEST_CONTROLLER3_URI), null, expectedAnswer).getBody()).isEqualTo(expectedAnswer2);

		//POST BAD DATA
		assertThat(restClient.execute(HttpMethod.POST, new URI(baseUrl + TEST_CONTROLLER2_URI), null, null).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);

		//PUT WITHOUT ANSWER
		assertThat(restClient.execute(HttpMethod.PUT, new URI(baseUrl + TEST_CONTROLLER2_URI), null, expectedAnswer).getBody()).isEqualTo(expectedAnswer);

		//PUT WITH ANSWER
		assertThat(restClient.execute(HttpMethod.PUT, new URI(baseUrl + TEST_CONTROLLER3_URI), null, expectedAnswer).getBody()).isEqualTo(expectedAnswer2);

		//PUT BAD DATA
		assertThat(restClient.execute(HttpMethod.PUT, new URI(baseUrl + TEST_CONTROLLER2_URI), null, null).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);

		//DELETE WITHOUT ANSWER
		assertThat(restClient.execute(HttpMethod.DELETE, new URI(baseUrl + TEST_CONTROLLER2_URI), null, expectedAnswer).getBody()).isEqualTo(expectedAnswer);

		//DELETE WITH ANSWER
		assertThat(restClient.execute(HttpMethod.DELETE, new URI(baseUrl + TEST_CONTROLLER3_URI), null, expectedAnswer).getBody()).isEqualTo(expectedAnswer2);

		//DELETE BAD DATA
		assertThat(restClient.execute(HttpMethod.DELETE, new URI(baseUrl + TEST_CONTROLLER2_URI), null, null).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	private void testDelay() throws URISyntaxException {
		long now = System.currentTimeMillis();
		restClient.execute(HttpMethod.GET, new URI(baseUrl + TEST_CONTROLLER4_URI), null, null).getBody();
		long processMs = System.currentTimeMillis() - now;
		assertThat(processMs).isGreaterThanOrEqualTo(10);
	}

	private void testRouterTimeout() throws URISyntaxException {
		ResponseEntity<String> response = restClient.execute(HttpMethod.GET, new URI(baseUrl + TEST_CONTROLLER5_URI), null, null);
		assertThat(response.getStatusCodeValue()).isEqualTo(408);
	}

}
