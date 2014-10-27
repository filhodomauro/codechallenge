package com.brasilct.model.navigator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.brasilct.model.LinesIntegration;
import com.brasilct.model.Routes;
import com.brasilct.model.navigator.way.RoutesSuccessWay;
import com.brasilct.repository.UndergroundRepository;

/**
 * 
 * @author mauro
 *
 */
public class RoutesNavigator extends DefaultNavigator<Routes, RoutesSuccessWay> {
	
	private UndergroundRepository repository;
	private Map<Routes, List<Routes>> integrations;
	
	public RoutesNavigator(UndergroundRepository repository) {
		super();
		this.repository = repository;
		this.integrations = new HashMap<Routes, List<Routes>>();
	}

	@Override
	protected void found(Routes found) {
		ways.add(new RoutesSuccessWay(new LinkedList<Routes>(currentWay)));
	}

	@Override
	protected List<Routes> getConnections(Routes base, Routes target) {
		List<Routes> connections = null;	
		if(this.integrations.containsKey(base)){
			connections = this.integrations.get(base);
		} else {
			LinesIntegration integrations = repository.findIntegrationsByRoute(base);
			connections = repository.findRoutesByIds(integrations.getLines());
			this.integrations.put(base, connections);
		}
		return connections;
	}
}
