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
		if (!RECEIVED_DATA.containsKey(item.satellite())) {
	 		RECEIVED_DATA.put(item.satellite(), item);
	 	}
	}

	public Collection<SatelliteData> findAll() {
		return RECEIVED_DATA.values();
	}

	public SatelliteData findBySatellite(Satellite satellite) {
		return RECEIVED_DATA.get(satellite);
	}
	
	public void clear() {
		RECEIVED_DATA.clear();
	}
	
}
