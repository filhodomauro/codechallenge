package com.brasilct.service;

import com.brasilct.model.Stations;
import com.brasilct.model.navigator.way.SuccessWay;



public interface StationsService {

	public SuccessWay<Stations> findAnyWay(Integer from, Integer to);
	
	public SuccessWay<Stations> findShortWay(Integer from, Integer to);
	
	public Integer getDuration(Integer from, Integer to, int stationTime, int integrationTime);
	
}
