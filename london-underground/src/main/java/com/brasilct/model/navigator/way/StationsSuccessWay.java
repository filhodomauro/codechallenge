package com.brasilct.model.navigator.way;

import java.util.Comparator;
import java.util.List;

import com.brasilct.model.Stations;

/**
 * 
 * @author mauro
 *
 */
public class StationsSuccessWay implements SuccessWay<Stations>{
	
	private List<Stations> ways;
	private int integrationsQuantity = 0;
	private static final Comparator<StationsSuccessWay> comparator;
	
	static {
		comparator = new Comparator<StationsSuccessWay>() {

			@Override
			public int compare(StationsSuccessWay o1, StationsSuccessWay o2) {
				return Integer.valueOf(o1.getWays().size()).compareTo(o2.getWays().size()) ;
			}
		};
	}

	public StationsSuccessWay(List<Stations> stations, int integrationsQuantity) {
		this.ways = stations;
		this.integrationsQuantity = integrationsQuantity;
	}

	public int getIntegrationsQuantity() {
		return integrationsQuantity;
	}

	@Override
	public List<Stations> getWays() {
		return ways;
	}

	public static Comparator<StationsSuccessWay> getComparator(){
		return comparator;
	}
}
