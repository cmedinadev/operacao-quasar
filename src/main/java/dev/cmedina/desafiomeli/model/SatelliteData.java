package dev.cmedina.desafiomeli.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record SatelliteData(Satellite satellite, Float distance, String[] message) {

	public SatelliteData withSatellite(Satellite satellite) {
		return new SatelliteData(satellite, distance(), message());
	}

}
