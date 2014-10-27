package com.brasilct.model.navigator;

import java.util.List;

import com.brasilct.model.Routes;
import com.brasilct.repository.UndergroundRepository;

/**
 * 
 * @author mauro
 *
 */
public class SingleStationsNavigator extends StationsNavigator {

	public SingleStationsNavigator(UndergroundRepository repository, List<Routes> routes) {
		super(repository, routes);
	}
	
	@Override
	protected boolean canContinue() {
		if(!getWays().isEmpty()){
			return false;
		}
		return super.canContinue();
	}

}
