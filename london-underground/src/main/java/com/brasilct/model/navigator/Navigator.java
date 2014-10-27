package com.brasilct.model.navigator;

import java.util.List;

import com.brasilct.model.navigator.way.SuccessWay;

/**
 * 
 * @author mauro
 *
 */
public interface Navigator<T, K extends SuccessWay<?>> {

	boolean find(T from, T to);
	List<K> getWays();
}
