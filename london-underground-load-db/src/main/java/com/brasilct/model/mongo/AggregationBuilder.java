package com.brasilct.model.mongo;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * 
 * @author mauro
 *
 */
public class AggregationBuilder {
	
	private DBObject projection;
	private DBObject match;
	private DBObject groupBy;
	private DBObject sort;
	
	private AggregationBuilder(){
	}

	public AggregationBuilder addProjection(DBObject projection){
		this.projection = new BasicDBObject("$project", projection);
		return this;
	}
	
	public AggregationBuilder addMatch(DBObject match){
		this.match = new BasicDBObject("$match", match);
		return this;
	}
	
	public AggregationBuilder addGroupBy(DBObject groupBy){
		this.groupBy = new BasicDBObject("$group", groupBy);
		return this;
	}
	
	public AggregationBuilder addSort(DBObject sort){
		this.sort = new BasicDBObject("$sort", sort);
		return this;
	}
	
	public List<DBObject> get(){
		List<DBObject> aggregations = new ArrayList<DBObject>();
		
		if(this.match != null){
			aggregations.add(this.match);
		}
		
		if(this.projection != null){
			aggregations.add(this.projection);
		}
		
		if(this.groupBy != null){
			aggregations.add(this.groupBy);
		}
		
		if(this.sort != null){
			aggregations.add(this.sort);
		}
		return aggregations;
	}
	
	public static AggregationBuilder builder(){
		return new AggregationBuilder();
	}
}
