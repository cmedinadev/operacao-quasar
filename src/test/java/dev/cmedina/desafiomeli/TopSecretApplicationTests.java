package dev.cmedina.desafiomeli;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import dev.cmedina.desafiomeli.exception.InvalidMessageException;
import dev.cmedina.desafiomeli.handler.ExceptionHandler.Error;
import dev.cmedina.desafiomeli.model.PayloadSatelliteData;
import dev.cmedina.desafiomeli.model.ResultSecret;
import dev.cmedina.desafiomeli.model.Satellite;
import dev.cmedina.desafiomeli.model.SatelliteData;
import dev.cmedina.desafiomeli.service.TopSecretService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TopSecretApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private TopSecretService service;

	private String urlBase;

	public TopSecretApplicationTests(@LocalServerPort int port) {
		urlBase = "http://localhost:" + port;
	}

	@Test
	void testGetLocationCorrect() {
		Float[] res = service.getLocation(721f, 300f, 412f);
		assertArrayEquals(new Float[] { 100f, 200f }, res);
	}

	@Test
	void testGetLocationNotFound() {
		Float[] res = service.getLocation(200f, 300f, 312f);
		assertNull(res);
	}

	@Test
	void testGetMessage() throws InvalidMessageException {
		String[] a = new String[] { "this", "", "", "secret", "" };
		String[] b = new String[] { "", "is", "", "", "message" };
		String[] c = new String[] { "this", "", "a", "", "" };
		String x = service.getMessage(a, b, c);
		assertEquals(x, "this is a secret message");
	}

	@Test
	void testGetMessageError() {
		String[] a = new String[] { "this", "", "", "secret", "" };
		String[] b = new String[] { "", "", "", "", "message" };
		String[] c = new String[] { "this", "", "a", "", "" };

		assertThrows(InvalidMessageException.class, () -> {
			service.getMessage(a, b, c);
		});
	}

	@Test
	void testPostTopSecretsStatusOK() {

		SatelliteData kenobi = new SatelliteData(Satellite.kenobi, 721f, new String[] { "this", "", "", "secret", "" });
		SatelliteData skywaker = new SatelliteData(Satellite.skywalker, 300f,
				new String[] { "", "is", "", "", "message" });
		SatelliteData sato = new SatelliteData(Satellite.sato, 412f, new String[] { "this", "", "a", "", "" });

		List<SatelliteData> listSatellites = Arrays.asList(new SatelliteData[] { kenobi, skywaker, sato });

		PayloadSatelliteData payloadObj = new PayloadSatelliteData(listSatellites);

		ResponseEntity<ResultSecret> result = restTemplate.postForEntity(urlBase + "/topsecret", payloadObj,
				ResultSecret.class);

		assertEquals(result.getStatusCode(), HttpStatus.OK);

		assertEquals(result.getBody().message(), "this is a secret message");

		assertEquals(result.getBody().position().x(), 100f);

		assertEquals(result.getBody().position().y(), 200f);

	}

	@Test
	void testPostTopSecretStatusNotFound() {

		SatelliteData kenobi = new SatelliteData(Satellite.kenobi, 721f, new String[] { "this", "", "", "secret", "" });
		SatelliteData skywaker = new SatelliteData(Satellite.skywalker, 300f,
				new String[] { "", "is", "", "", "message" });
		SatelliteData sato = new SatelliteData(Satellite.sato, 400f, new String[] { "this", "", "a", "", "" });

		List<SatelliteData> listSatellites = Arrays.asList(new SatelliteData[] { kenobi, skywaker, sato });

		PayloadSatelliteData payloadObj = new PayloadSatelliteData(listSatellites);

		ResponseEntity<ResultSecret> result = restTemplate.postForEntity(urlBase + "/topsecret", payloadObj,
				ResultSecret.class);

		assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);

	}

	@Test
	void testPostTopSecretSplitStatusOK() {

		SatelliteData kenobi = new SatelliteData(Satellite.kenobi, 721f, new String[] { "this", "", "", "secret", "" });

		ResponseEntity<Void> result = restTemplate.postForEntity(urlBase + "/topsecret_split/kenobi", kenobi,
				Void.class);

		assertEquals(result.getStatusCode(), HttpStatus.ACCEPTED);

	}

	@Test
	void testGetTopSecretSplitStatusError() throws Exception {

		ResponseEntity<Error> result = restTemplate.getForEntity(urlBase + "/topsecret_split/", Error.class);

		assertEquals(result.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);

		assertEquals(result.getBody().message(), "Não há informação suficiente");

	}

	@Test
	void testGetTopSecretSplitStatusOK() {

		SatelliteData skywaker = new SatelliteData(Satellite.skywalker, 300f,
				new String[] { "", "is", "", "", "message" });

		SatelliteData sato = new SatelliteData(Satellite.sato, 412f, new String[] { "this", "", "a", "", "" });

		SatelliteData kenobi = new SatelliteData(Satellite.kenobi, 721f, new String[] { "this", "", "", "secret", "" });

		restTemplate.postForEntity(urlBase + "/topsecret_split/sato", sato, Void.class);

		restTemplate.postForEntity(urlBase + "/topsecret_split/kenobi", kenobi, Void.class);

		restTemplate.postForEntity(urlBase + "/topsecret_split/skywalker", skywaker, Void.class);

		ResponseEntity<ResultSecret> result = restTemplate.getForEntity(urlBase + "/topsecret_split/",
				ResultSecret.class);

		assertEquals(result.getStatusCode(), HttpStatus.OK);

		assertEquals(result.getBody().message(), "this is a secret message");

		assertEquals(result.getBody().position().x(), 100f);

		assertEquals(result.getBody().position().y(), 200f);

	}

}
