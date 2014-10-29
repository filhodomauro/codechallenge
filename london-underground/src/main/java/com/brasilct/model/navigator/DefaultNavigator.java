package com.brasilct.model.navigator;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brasilct.model.navigator.way.SuccessWay;

/**
 * 
 * @author mauro
 *
 */
public abstract class DefaultNavigator<T,K extends SuccessWay<?>> implements Navigator<T, K>{
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultNavigator.class);
	
	protected List<K> ways;
	protected List<T> currentWay;
	
	public DefaultNavigator() {
		this.ways = new LinkedList<K>();
		this.currentWay = new LinkedList<T>();
	}
	
	@Override
	public boolean find(T from, T to) {
		LOG.info("Finding...");
		go(from, null, to);
		return !getWays().isEmpty();
	}
	
	@Override
	public List<K> getWays() {
		return this.ways;
	}
	protected void go(T from, T previous, T to){
		addStep(from, previous);
		if(from.equals(to)){
			found(from);
		} else {
			if(canContinue()){
				List<T> connections = getConnections(from, to);
				if(connections.contains(to)){
					go(connections.remove(connections.indexOf(to)), from, to);
				} 
				for(T connection : connections){
					if(!skip(connection, from)){
						go(connection, from, to);
					}
				}
			}
		}
		if(needsBack()){
			back(from, previous);
		}
	}

	protected void addStep(T step, T previous){
		this.currentWay.add(step);
	}
	
	protected boolean canContinue(){
		return true;
	}
	
	protected boolean needsBack(){
		return true;
	}
	
	protected void back(T from, T previous){
		this.currentWay.remove(from);
	}
	
	protected boolean skip(T next, T previous){
		return next.equals(previous) || this.currentWay.contains(next) || !canContinue();
	}
	
	protected abstract void found(T found);
	
	protected abstract List<T> getConnections(T base, T target);
	
}
