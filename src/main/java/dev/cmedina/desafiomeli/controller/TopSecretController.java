package dev.cmedina.desafiomeli.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dev.cmedina.desafiomeli.exception.InvalidMessageException;
import dev.cmedina.desafiomeli.exception.NoEnoughInformationException;
import dev.cmedina.desafiomeli.model.PayloadSatelliteData;
import dev.cmedina.desafiomeli.model.Position;
import dev.cmedina.desafiomeli.model.ResultSecret;
import dev.cmedina.desafiomeli.model.SatelliteData;
import dev.cmedina.desafiomeli.service.TopSecretService;

@RestController
@RequestMapping(value = "/")
public class TopSecretController {

	private TopSecretService service;

	public TopSecretController(@Autowired TopSecretService service) {
		this.service = service;
	}

	@RequestMapping(value = "/topsecret", method = RequestMethod.POST)
	public ResponseEntity<ResultSecret> processarDados(@RequestBody PayloadSatelliteData payload)
			throws InvalidMessageException {

		Float[] distances = payload.satellites().stream().map(item -> item.distance()).toArray(Float[]::new);
		String[][] messages = payload.satellites().stream().map(item -> item.message()).toArray(String[][]::new);

		Float[] location = service.getLocation(distances);
		String message = service.getMessage(messages);

		// If the location could not be determined
		// then return: RESPONSE CODE: 404
		if (location == null || message == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Position position = new Position(location[0], location[1]);

		return new ResponseEntity<ResultSecret>(new ResultSecret(position, message), HttpStatus.OK);

	}

	@RequestMapping(value = "/topsecret_split/{satelliteName}", method = RequestMethod.POST)
	public ResponseEntity<Void> processarDadosSatellite(@PathVariable("satelliteName") String satelliteName,
			@RequestBody SatelliteData payload) {
		
		service.receiveSatelliteMessage(payload.withName(satelliteName));

		return new ResponseEntity<>(HttpStatus.ACCEPTED);

	}

	@RequestMapping(value = "/topsecret_split/", method = RequestMethod.GET)
	public ResponseEntity<?> processarDadosSatellite() throws NoEnoughInformationException, InvalidMessageException {

		Optional<ResultSecret> result = service.getLocationAndMessage();
		if (result.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body(result.get());
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

	}

}
