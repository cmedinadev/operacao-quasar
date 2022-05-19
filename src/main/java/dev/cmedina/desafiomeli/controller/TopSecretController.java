package dev.cmedina.desafiomeli.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dev.cmedina.desafiomeli.TopSecretService;
import dev.cmedina.desafiomeli.model.PayloadSatelliteData;
import dev.cmedina.desafiomeli.model.Position;
import dev.cmedina.desafiomeli.model.ResultSecret;
import dev.cmedina.desafiomeli.model.SatelliteData;

@RestController
@RequestMapping(value = "/")
public class TopSecretController {

	
	 private static final Map<String, SatelliteData> RECEIVED_DATA = new LinkedHashMap<>();
	
	
	 @Autowired
	 private TopSecretService program;
	
	 @RequestMapping(value = "/topsecret", method = RequestMethod.POST)
	 public ResponseEntity<ResultSecret> processarDados(@RequestBody PayloadSatelliteData payload) {
		 
		Float[] distances = payload.satellites().stream().map(item -> item.distance()).toArray(Float[]::new);
		String[][] messages = payload.satellites().stream().map(item -> item.message()).toArray(String[][]::new);
		
		Float[] location = program.getLocation(distances);
		String message = program.getMessage(messages);
		
		// Em caso que não seja possível determinar a posição ou a mensagem, 
		// então retorna: RESPONSE CODE: 404.
		if (location == null || message == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		Position position = new Position(location[0], location[1]);
		
		return new ResponseEntity<ResultSecret>(new ResultSecret(position, message), HttpStatus.OK);  
		 
	 }
	 
	 @RequestMapping(value = "/topsecret_split/{satelliteName}", method = RequestMethod.POST)
	 public ResponseEntity<Void> processarDadosSatellite(@PathVariable("satelliteName") String satelliteName, 
			 @RequestBody SatelliteData payload) {
		 
		 	if (!RECEIVED_DATA.containsKey(satelliteName)) {
		 		RECEIVED_DATA.put(satelliteName, payload);
		 	}
		 	
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		 
	 }
	 
	 @RequestMapping(value = "/topsecret_split/", method = RequestMethod.GET)
	 public ResponseEntity<?> processarDadosSatellite() {
		 	
		 if (RECEIVED_DATA.containsKey("kenobi") && 
				 RECEIVED_DATA.containsKey("skywalker") && 
				 RECEIVED_DATA.containsKey("sato")) {
			 
			  	var distances = new Float[] { RECEIVED_DATA.get("kenobi").distance(), RECEIVED_DATA.get("skywalker").distance(), RECEIVED_DATA.get("sato").distance() };
			  			
			  	var messages = new String[][] { RECEIVED_DATA.get("kenobi").message(), RECEIVED_DATA.get("skywalker").message(), RECEIVED_DATA.get("sato").message() };
		      
			  	Float[] location = program.getLocation(distances);
		        String message = program.getMessage(messages);

		        RECEIVED_DATA.clear();
		        
		     // Em caso que não seja possível determinar a posição ou a mensagem, 
				// então retorna: RESPONSE CODE: 404.
				if (location == null || message == null) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
				}
				
				Position position = new Position(location[0], location[1]);
			
				return ResponseEntity.status(HttpStatus.OK).body(new ResultSecret(position, message));  
				 
		 }
		 
		 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Não há informação suficiente");
	
	 }
	 
}
