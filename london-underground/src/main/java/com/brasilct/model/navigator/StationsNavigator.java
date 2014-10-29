package com.brasilct.model.navigator;

import java.util.LinkedList;
import java.util.List;

import com.brasilct.model.Routes;
import com.brasilct.model.Stations;
import com.brasilct.model.navigator.way.StationsSuccessWay;
import com.brasilct.repository.UndergroundRepository;
import com.brasilct.repository.type.Cardinals;

/**
 * 
 * @author mauro
 *
 */
public abstract class StationsNavigator extends DefaultNavigator<Stations, StationsSuccessWay>{
	
	private UndergroundRepository repository;
	private List<Routes> futureRoutes;
	private List<Routes> pastRoutes;
	private List<Stations> integrations;
	private Routes currentRoute;
	private int integrationsQuantity = 0;
	
	public StationsNavigator(UndergroundRepository repository, List<Routes> routes) {
		super();
		this.repository = repository;
		this.futureRoutes = routes;
		this.pastRoutes = new LinkedList<Routes>();
		this.integrations = new LinkedList<Stations>();
		goToNextRoute();
	}

	@Override
	protected void addStep(Stations step, Stations previous) {
		super.addStep(step, previous);
		if(isIntegration(step, previous)){
			integrations.add(step);
			integrationsQuantity++;
			goToNextRoute();
		}
	}
	
	@Override
	protected void back(Stations from, Stations previous) {
		super.back(from, previous);
		if(previous != null && integrations.remove(from)){
			integrationsQuantity--;
			goToPreviousRoute();
		}
	}
	
	@Override
	protected void found(Stations found) {
		ways.add(new StationsSuccessWay(new LinkedList<Stations>(currentWay), integrationsQuantity));
	}

	@Override
	protected List<Stations> getConnections(Stations base, Stations target) {
		List<Stations> connections = repository.findStationConnections(base, getTargetCardinalsReference(base, target));
		connections.removeAll(currentWay);
		return connections;
	}
	
	@Override
	protected boolean skip(Stations next, Stations previous) {
		if(super.skip(next, previous)){
			return true;
		}
		
		List<Routes> routes = repository.findRoutesByStation(next);
		if(!routes.contains(this.currentRoute)){
			if(routes.isEmpty()){
				return true;
			}
			
			Routes nextRoute = routes.get(0);
			if(!this.futureRoutes.contains(nextRoute)){
				return true;
			}
		}
		
		return false;
	}
	
	private void goToNextRoute(){
		this.pastRoutes.add(currentRoute);
		this.currentRoute = this.futureRoutes.remove(0);
	}
	
	private void goToPreviousRoute(){
		this.futureRoutes.add(currentRoute);
		this.currentRoute = this.pastRoutes.remove(this.pastRoutes.size() -1);
	}
	private boolean isIntegration(Stations next, Stations previous){
		if(previous != null && previous.getTotalLines() > 1){
			if(!belongsToCurrentRoute(next)){
				return true;
			}
		}
		return false;
	}
	
	private boolean belongsToCurrentRoute(Stations step){
		List<Routes> routesNext = repository.findRoutesByStation(step);
		if(routesNext.contains(this.currentRoute)){
			return true;
		}
		return false;
	}
	
	private List<Cardinals> getTargetCardinalsReference(Stations current, Stations target){
		Double latitudeDiff = current.getLatitude() - target.getLatitude();
		Double longitudeDiff = current.getLongitude() - target.getLongitude();
		
		Cardinals orientationLatitude = Cardinals.fromLatitudeDiff(latitudeDiff);
		Cardinals orientationLongitude = Cardinals.fromLongitudeDiff(longitudeDiff);
		
		List<Cardinals> cardinalsReferente = new LinkedList<Cardinals>();
		if(latitudeDiff < longitudeDiff){
			cardinalsReferente.add(orientationLongitude);
		} else {
			cardinalsReferente.add(orientationLatitude);
		}
		return cardinalsReferente;
	}
	
}
