package com.brasilct.repository.type;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

/**
 * 
 * @author mauro
 *
 */
public enum Cardinals {
	NORTH(new Order(Sort.Direction.ASC, "longitude")),
	SOUTH(new Order(Sort.Direction.DESC, "longitude")),
	EAST(new Order(Sort.Direction.ASC, "latitude")),
	WEST(new Order(Sort.Direction.DESC, "latitude"));
	
	private Order order;
	
	Cardinals(Order order){
		this.order = order;
	}
	
	public Order getOrder(){
		return this.order;
	}
	
	public static Cardinals fromLatitudeDiff (double latitudeDiff){
		if(latitudeDiff > 0){
			return NORTH;
		} else {
			return SOUTH;
		}
	}
	
	public static Cardinals fromLongitudeDiff(double longitudeDiff){
		if(longitudeDiff > 0){
			return WEST;
		} else {
			return EAST;
		}
	}
}
