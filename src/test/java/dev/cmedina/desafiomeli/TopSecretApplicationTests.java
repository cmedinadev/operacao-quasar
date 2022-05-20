package dev.cmedina.desafiomeli;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

import dev.cmedina.desafiomeli.model.PayloadSatelliteData;
import dev.cmedina.desafiomeli.model.ResultSecret;
import dev.cmedina.desafiomeli.model.SatelliteData;
import dev.cmedina.desafiomeli.service.TopSecretService;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
class TopSecretApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Autowired
	private TopSecretService service;
	
	@LocalServerPort
	private int port;
	
	
	 @Test
	 void testGetLocationCorrect(){
		 Float[] res = service.getLocation(721f, 300f, 412f);
		 assertArrayEquals(new Float[] {100f, 200f}, res); 
	 }
	
	 @Test
	 void testGetLocationNotFound(){
		 Float[] res = service.getLocation(200f, 300f, 312f);
		 assertNull(res); 
	 }

	 @Test
	 void testGetMessage(){
		 String[] a = new String[] {"this", "", "", "secret", ""};
		 String[] b = new String[] {"", "is", "", "", "message"};
		 String[] c = new String[] {"this", "", "a", "", ""};
		 String x = service.getMessage(a, b, c);
		 assertEquals(x, "this is a secret message"); 
	 }

	 @Test
	 void testGetMessageError(){
		 String[] a = new String[] {"this", "", "", "secret", ""};
		 String[] b = new String[] {"", "", "", "", "message"};
		 String[] c = new String[] {"this", "", "a", "", ""};
		 String x = service.getMessage(a, b, c);
		 assertNull(x);  
	 }
	
	
	@Test
	void testPostTopSecretsStatusOK() throws Exception {
		
		SatelliteData kenobi = new SatelliteData("kenobi", 721f, new String[] {"this", "", "", "secret", ""});
		SatelliteData skywaker = new SatelliteData("skywalker", 300f, new String[] {"", "is", "", "", "message"});
		SatelliteData sato = new SatelliteData("sato", 412f, new String[] {"this", "", "a", "", ""});
		
		
		List<SatelliteData> listSatellites = Arrays.asList(new SatelliteData[] {kenobi, skywaker, sato});
		
		PayloadSatelliteData payloadObj = new PayloadSatelliteData(listSatellites);
		
		ResponseEntity<ResultSecret> result = restTemplate.postForEntity("http://localhost:"+port+"/topsecret", payloadObj, ResultSecret.class);
		
		assertEquals(result.getStatusCode(), HttpStatus.OK);
		
		assertEquals(result.getBody().message(), "this is a secret message");
		
		assertEquals(result.getBody().position().x(), 100f);
		
		assertEquals(result.getBody().position().y(), 200f);
		
	}
	
	@Test
	void testPostTopSecretStatusNotFound() throws Exception {
		
		SatelliteData kenobi = new SatelliteData("kenobi", 721f, new String[] {"this", "", "", "secret", ""});
		SatelliteData skywaker = new SatelliteData("skywalker", 300f, new String[] {"", "is", "", "", "message"});
		SatelliteData sato = new SatelliteData("sato", 400f, new String[] {"this", "", "a", "", ""});
		
		
		List<SatelliteData> listSatellites = Arrays.asList(new SatelliteData[] {kenobi, skywaker, sato});
		
		PayloadSatelliteData payloadObj = new PayloadSatelliteData(listSatellites);
		
		ResponseEntity<ResultSecret> result = restTemplate.postForEntity("http://localhost:"+port+"/topsecret", payloadObj, ResultSecret.class);
		
		assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);
		
	}

	
	@Test
	void testPostTopSecretSplitStatusOK() throws Exception {

		SatelliteData kenobi = new SatelliteData("kenobi", 721f, new String[] {"this", "", "", "secret", ""});
		
		ResponseEntity<Void> result = restTemplate.postForEntity("http://localhost:"+port+"/topsecret_split/kenobi", kenobi,  Void.class);
		
		assertEquals(result.getStatusCode(), HttpStatus.ACCEPTED);

	}
	
	@Test
	void testGetTopSecretSplitStatusError() throws Exception {
		
		ResponseEntity<String> result = restTemplate.getForEntity("http://localhost:"+port+"/topsecret_split/", String.class);
		
		assertEquals(result.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
		
		assertEquals(result.getBody(), "Não há informação suficiente");

	}

	@Test
	void testGetTopSecretSplitStatusOK() throws Exception {
		
		SatelliteData skywaker = new SatelliteData("skywalker", 300f, new String[] {"", "is", "", "", "message"});
		
		SatelliteData sato = new SatelliteData("sato", 412f, new String[] {"this", "", "a", "", ""});

		SatelliteData kenobi = new SatelliteData("kenobi", 721f, new String[] {"this", "", "", "secret", ""});
		
		restTemplate.postForEntity("http://localhost:"+port+"/topsecret_split/kenobi", kenobi,  Void.class);
	
		restTemplate.postForEntity("http://localhost:"+port+"/topsecret_split/skywalker", skywaker, Void.class);
		
		restTemplate.postForEntity("http://localhost:"+port+"/topsecret_split/sato", sato, Void.class);
		
		ResponseEntity<ResultSecret> result = restTemplate.getForEntity("http://localhost:"+port+"/topsecret_split/", ResultSecret.class);
		
		assertEquals(result.getStatusCode(), HttpStatus.OK);
		
		assertEquals(result.getBody().message(), "this is a secret message");
		
		assertEquals(result.getBody().position().x(), 100f);
		
		assertEquals(result.getBody().position().y(), 200f);

	}

}
