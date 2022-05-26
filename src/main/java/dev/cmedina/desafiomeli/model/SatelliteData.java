package dev.cmedina.desafiomeli.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record SatelliteData(String name, Float distance, String[] message) {

	public SatelliteData withName(String name) {
		return new SatelliteData(name, distance(), message());
	}

}
