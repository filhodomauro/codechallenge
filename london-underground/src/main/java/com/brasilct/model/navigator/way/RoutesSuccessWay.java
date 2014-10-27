package com.brasilct.model.navigator.way;

import java.util.List;

import com.brasilct.model.Routes;

/**
 * 
 * @author mauro
 *
 */
public class RoutesSuccessWay implements SuccessWay<Routes>{
	
	private List<Routes> ways;
	
	public RoutesSuccessWay(List<Routes> ways) {
		this.ways = ways;
	}

	@Override
	public List<Routes> getWays() {
		return this.ways;
	}
	
}
