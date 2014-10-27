package com.brasilct.controller.results;

import com.brasilct.model.Stations;

/**
 * 
 * @author mauro
 *
 */
public class StationResult {

	public StationResult() {
	}
	
	public StationResult(Stations stations) {
		this.name = stations.getName();
	}
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}	
