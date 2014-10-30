package com.brasilct.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.brasilct.model.Routes;
import com.brasilct.model.Stations;
import com.brasilct.model.navigator.RoutesNavigator;
import com.brasilct.model.navigator.ShortStationsNavigator;
import com.brasilct.model.navigator.SingleStationsNavigator;
import com.brasilct.model.navigator.StationsNavigator;
import com.brasilct.model.navigator.way.RoutesSuccessWay;
import com.brasilct.model.navigator.way.StationsSuccessWay;
import com.brasilct.model.navigator.way.SuccessWay;
import com.brasilct.repository.UndergroundRepository;

/**
 * 
 * @author mauro
 *
 */
@Component
public class StationsServiceImpl implements StationsService{
	
	private static final Logger LOG = LoggerFactory.getLogger(StationsServiceImpl.class); 
	
	@Autowired
	private UndergroundRepository repository;
	
	@Override
	public SuccessWay<Stations> findAnyWay(Integer from, Integer to) {
		LOG.info("Find any way: [ {} - {} ]", from, to);
		Stations stationFrom = findStation(from);
		Stations stationTo = findStation(to);
		List<RoutesSuccessWay> routes = getRoutes(stationFrom, stationTo);
		LOG.info("Total Routes: {}", routes.size());
		for(RoutesSuccessWay route : routes){
			StationsNavigator navigator = new SingleStationsNavigator(repository, route.getWays());
			if(navigator.find(stationFrom, stationTo)){
				return navigator.getWays().get(0);
			}
		}
		throw new RuntimeException("Não foi possível localizar uma rota entre essas estações");
	}


	@Override
	public SuccessWay<Stations> findShortWay(Integer from, Integer to) {
		LOG.info("Find short way: [ {} - {} ]", from, to);
		Stations stationFrom = findStation(from);
		Stations stationTo = findStation(to);
		List<RoutesSuccessWay> routes = getRoutes(stationFrom, stationTo);
		LOG.info("Total Route Ways: {}", routes.size());
		Collections.sort(routes, new Comparator<RoutesSuccessWay>() {
			@Override
			public int compare(RoutesSuccessWay o1, RoutesSuccessWay o2) {
				return Integer.valueOf(o1.getWays().size()).compareTo(o2.getWays().size());
			}
		});
		List<StationsSuccessWay> ways = new LinkedList<StationsSuccessWay>();
		StationsSuccessWay shortWay = null;
		for(RoutesSuccessWay route : routes){
			StationsNavigator navigator = new ShortStationsNavigator(repository, route.getWays(), shortWay);
			if(navigator.find(stationFrom, stationTo)){
				ways.add(navigator.getWays().get(0));
				Collections.sort(ways, StationsSuccessWay.getComparator());
				shortWay = ways.get(0);
			}
		}
		
		if(shortWay != null){
			return shortWay;
		}
		
		throw new RuntimeException("Não foi possível localizar uma rota entre essas estações");	
	}
	
	


	@Override
	public Integer getDuration(Integer from, Integer to, int stationTime, int integrationTime) {
		SuccessWay<Stations> shortWay = findShortWay(from, to);
		Integer stationsQuantity = shortWay.getWays().size();
		Integer integrationQuantity = 0;
		if(shortWay instanceof StationsSuccessWay){
			integrationQuantity = ((StationsSuccessWay) shortWay).getIntegrationsQuantity();
		}
		LOG.info("Get Duratiom -> Stations: QT [{}] - Single Time[{}]", stationsQuantity, stationTime);
		LOG.info("Get Duratiom -> Integrations: QT [{}] - Single Time[{}]", integrationQuantity, integrationTime);
		return (stationsQuantity -1 - integrationQuantity ) * stationTime 
			+  integrationQuantity * integrationTime;
	}

	private List<RoutesSuccessWay> getRoutes(Stations from, Stations to){
		LOG.info("Find Routes");
		RoutesNavigator navigator = new RoutesNavigator(repository);
		List<Routes> routesFrom = repository.findRoutesByStation(from);
		List<Routes> routesTo = repository.findRoutesByStation(to);
		
		for(Routes routeFrom : routesFrom){
			for(Routes routeTo : routesTo){
				navigator.find(routeFrom, routeTo);
			}
		}
		return navigator.getWays();
	}
	
	private Stations findStation(Integer id){
		Stations station = repository.findStationById(id);
		if(station == null){
			throw new RuntimeException(String.format("Station %s not founded",id));
		}
		return station;
	}
}
