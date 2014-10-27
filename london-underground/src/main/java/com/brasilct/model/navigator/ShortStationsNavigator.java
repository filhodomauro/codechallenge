package com.brasilct.model.navigator;

import java.util.Collections;
import java.util.List;

import com.brasilct.model.Routes;
import com.brasilct.model.Stations;
import com.brasilct.model.navigator.way.StationsSuccessWay;
import com.brasilct.repository.UndergroundRepository;

/**
 * 
 * @author mauro
 *
 */
public class ShortStationsNavigator extends StationsNavigator {
	
	private StationsSuccessWay shortWay;

	public ShortStationsNavigator(UndergroundRepository repository, List<Routes> routes) {
		super(repository, routes);
	}
	
	@Override
	protected void found(Stations found) {
		super.found(found);
		Collections.sort(getWays(), StationsSuccessWay.getComparator());
		shortWay = getWays().get(0);
	}
	
	@Override
	protected boolean canContinue() {
		if(shortWay != null){
			if(currentWay.size() > shortWay.getWays().size()){
				return false;
			}
		}
		return super.canContinue();
	}
}
