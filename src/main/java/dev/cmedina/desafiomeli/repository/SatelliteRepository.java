package dev.cmedina.desafiomeli.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import dev.cmedina.desafiomeli.model.Satellite;
import dev.cmedina.desafiomeli.model.SatelliteData;

@Service
@Scope("singleton")
public class SatelliteRepository {

	private static Map<Satellite, SatelliteData> RECEIVED_DATA = new HashMap<>();
	
	
	public void insert(SatelliteData item) {
		Satellite satellite = Satellite.valueOf(item.name());
		if (!RECEIVED_DATA.containsKey(satellite)) {
	 		RECEIVED_DATA.put(satellite, item);
	 	}
	}

	public Collection<SatelliteData> findAll() {
		return RECEIVED_DATA.values();
	}

	public SatelliteData findByName(String name) {
		return RECEIVED_DATA.get(Satellite.valueOf(name));
	}
	
	public void clear() {
		RECEIVED_DATA.clear();
	}
	
}
