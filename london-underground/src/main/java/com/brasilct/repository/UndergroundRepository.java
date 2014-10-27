package com.brasilct.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.brasilct.model.Lines;
import com.brasilct.model.LinesIntegration;
import com.brasilct.model.Routes;
import com.brasilct.model.Stations;
import com.brasilct.repository.type.Cardinals;

@Repository
public class UndergroundRepository {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public Stations findStationById(Integer id){
		Criteria criteria = Criteria.where("id").is(id);
		Query query = Query.query(criteria);
		return mongoTemplate.findOne(query, Stations.class);
	}
	
	/**
	 *
	 * @param station
	 * @return
	 */
	public List<Stations> findStationConnections(Stations station, List<Cardinals> orientations){
		Criteria criteria = new Criteria();
		criteria.orOperator(Criteria.where("station1").is(station.getId()),Criteria.where("station2").is(station.getId()));
		Query query = Query.query(criteria);
		List<Lines> lines = mongoTemplate.find(query, Lines.class);
		
		if(lines != null && !lines.isEmpty()){
			List<Integer> stationsId = new ArrayList<Integer>();
			for(Lines line : lines){
				Integer connectionId = 
						station.getId().equals(line.getStation1()) 
						? line.getStation2() 
						: line.getStation1();
				stationsId.add(connectionId);
			}
			criteria = Criteria.where("id").in(stationsId);
			query = Query.query(criteria);
			
			if(orientations != null && !orientations.isEmpty()){
				Sort sort = null;
				for(Cardinals orientation : orientations){
					if(sort == null){
						sort = new Sort(orientation.getOrder());
					} else {
						sort.and(new Sort(orientation.getOrder()));
					}
				}
				query.with(sort);
			}
			
			return mongoTemplate.find(query, Stations.class);
		}
		return null;
	}

	public List<Routes> findRoutesByStation(Stations station){
		Criteria criteria = new Criteria();
		criteria.orOperator(Criteria.where("station1").is(station.getId()),Criteria.where("station2").is(station.getId()));
		Query query = Query.query(criteria);
		List<Lines> lines = mongoTemplate.find(query, Lines.class);
		
		if(lines != null && !lines.isEmpty()){
			List<Integer> linesId = new ArrayList<Integer>();
			for(Lines line : lines){
				linesId.add(line.getLine());
			}
			criteria = Criteria.where("line").in(linesId);
			query = Query.query(criteria).with(new Sort("line"));
			return mongoTemplate.find(query, Routes.class);
		}
		return null;
	}
	
	public LinesIntegration findIntegrationsByRoute(Routes route){
		Criteria criteria = Criteria.where("line").is(route.getLine());
		Query query = Query.query(criteria);
		return mongoTemplate.findOne(query, LinesIntegration.class);
	}
	
	public List<Routes> findRoutesByIds(List<Integer> routesIds){
		Criteria criteria = Criteria.where("line").in(routesIds);
		Query query = Query.query(criteria).with(new Sort("line"));
		return mongoTemplate.find(query, Routes.class);
	}
	
}
