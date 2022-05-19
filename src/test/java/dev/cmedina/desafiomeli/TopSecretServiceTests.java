package dev.cmedina.desafiomeli;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.cmedina.desafiomeli.service.TopSecretService;

@SpringBootTest
public class TopSecretServiceTests { 

 	 @Autowired
	 private TopSecretService service;
	
	
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

	 
}
