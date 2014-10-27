package com.brasilct.controller;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brasilct.controller.results.StationResult;
import com.brasilct.controller.results.TimeResult;
import com.brasilct.model.Stations;
import com.brasilct.model.navigator.way.SuccessWay;
import com.brasilct.service.StationsService;


/**
 * 
 * @author mauro
 *
 */
@RestController
public class UndergroundController {
	
	private static final Integer DEFAULT_STATION_TIME = 3;
	private static final Integer DEFAULT_INTEGRATION_TIME = 12;
	
	private static final Logger LOG = LoggerFactory.getLogger(UndergroundController.class);

	
	@Autowired
	private StationsService service;
	
	@RequestMapping("/route/{from}/{to}")
	public List<StationResult> findRoute(@PathVariable String from, @PathVariable String to){
		LOG.info("Chamada /route acionada");
		Integer idFrom = Integer.valueOf(from);
		Integer idTo = Integer.valueOf(to);
		SuccessWay<Stations> way = service.findAnyWay(idFrom, idTo);
		LOG.info("Chamada /route finalizada");
		return toResults(way.getWays());
	}
	
	@RequestMapping("/bestRoute/{from}/{to}")
	public List<StationResult> findBestRoute(@PathVariable String from, @PathVariable String to){
		Integer idFrom = Integer.valueOf(from);
		Integer idTo = Integer.valueOf(to);
		SuccessWay<Stations> way = service.findShortWay(idFrom, idTo);
		return toResults(way.getWays());
	}
	
	@RequestMapping("/bestRouteTime/{from}/{to}/{stationTime}/{integrationTime}")
	public TimeResult getBestRouteTime(@PathVariable String from, 
									@PathVariable String to, 
									@PathVariable Integer stationTime,
									@PathVariable Integer integrationTime){
		Integer idFrom = Integer.valueOf(from);
		Integer idTo = Integer.valueOf(to);
		if(stationTime == null){
			stationTime = DEFAULT_STATION_TIME;
		}
		
		if(integrationTime == null){
			integrationTime = DEFAULT_INTEGRATION_TIME;
		}
		return toResults(service.getDuration(idFrom, idTo, stationTime, integrationTime));
	}
	
	private List<StationResult> toResults(List<Stations> stations){
		List<StationResult> results = new LinkedList<StationResult>();
		for(Stations station : stations){
			results.add(new StationResult(station));
		}
		return results;
	}
	
	private TimeResult toResults(Integer time){
		return new TimeResult(time);
	}
	
}
