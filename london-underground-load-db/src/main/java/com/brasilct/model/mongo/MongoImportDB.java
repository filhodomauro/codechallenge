package com.brasilct.model.mongo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import com.brasilct.model.ImportDB;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * 
 * @author mauro
 *
 */
public class MongoImportDB implements ImportDB {
	
	private final ResourceBundle RESOURCE;
	private final String CSV_DELIMITER;
	private final List<String> DOUBLE_FIELDS;
	private final List<String> INT_FIELDS;
	private DB db;
	
	public MongoImportDB() {
		RESOURCE = ResourceBundle.getBundle("config");
		CSV_DELIMITER = RESOURCE.getString("db.file.delimeter");
		DOUBLE_FIELDS = Arrays.asList("latitude", "longitude");
		INT_FIELDS = Arrays.asList("id", "line", "station1", "station2", "total_lines");
		
	}

	public void load(String... files) {
		if(files != null && files.length > 0){
			DB db = getDB();
			for(String file : files){
				String collectionName = getCollectionName(file);
				System.out.println("Droping "+ collectionName);
				dropCollection(collectionName);
				System.out.println("Loading " + collectionName);
				final List<String> lines = readFile(file);
				final String[] keys = lines.get(0).replace("\"", "").split(CSV_DELIMITER);

				DBCollection collection = this.db.getCollection(collectionName);
				BulkWriteOperation builder = collection.initializeOrderedBulkOperation();
				for(int i = 1; i < lines.size(); i++){
					builder.insert(getDocumentFromCSVLine(keys, lines.get(i)));
				}
				builder.execute();
			}
		}
	}
	
	private void dropCollection(String collection){
		if(getDB().collectionExists(collection)){
			getDB().getCollection(collection).drop();
		}
	}
	
	/**
	 * 
	 */
	public void createLinesIntegration() {
		DB db = getDB();
		DBCursor routes = db.getCollection("routes").find();
		if(routes.size() > 0){
			List<DBObject> lineIntegrations = new ArrayList<DBObject>();
			while(routes.hasNext()){
				DBObject route = routes.next();
				Integer currentLine = Integer.valueOf(route.get("line").toString());
				DBCursor stationConnections = 
						db.getCollection("lines")
							.find(new BasicDBObject("line", currentLine));
				if(stationConnections.size() > 0){
					List<Object> stations = new ArrayList<Object>();
					while(stationConnections.hasNext()){
						DBObject stationConnection = stationConnections.next();
						stations.add(stationConnection.get("station1"));
						stations.add(stationConnection.get("station2"));
					}
					
					List<DBObject> matchFields = 
							Arrays.asList(BasicDBObjectBuilder.start().add("station1", BasicDBObjectBuilder.start().add("$in", stations).get()).get(),
										  BasicDBObjectBuilder.start().add("station2", BasicDBObjectBuilder.start().add("$in", stations).get()).get());

					List<DBObject> aggregations = 
							AggregationBuilder.builder()
								.addProjection(new BasicDBObject("line",1).append("_id", 0))
								.addMatch(new BasicDBObject("$or", matchFields))
								.addGroupBy(new BasicDBObject("_id", "$line"))
								.addSort(new BasicDBObject("_id", 1))
							.get();
					
					AggregationOutput output = db.getCollection("lines").aggregate(aggregations);
					List<Integer> lines = new LinkedList<Integer>();
					for(DBObject line : output.results()){
						Integer integrationLine = Integer.valueOf(line.get("_id").toString());
						if(!currentLine.equals(integrationLine)){
							lines.add(integrationLine);
						}
					}
					lineIntegrations.add(
							BasicDBObjectBuilder.start()
								.append("line", currentLine )
								.append("lines", lines).get()
					);
				}
				insertIntegrations(db, lineIntegrations);
			}
		}
	}

	/**
	 * 
	 * @param db
	 * @param lineIntegrations
	 */
	private void insertIntegrations(DB db, List<DBObject> lineIntegrations){
		String collectionName = "linesIntegration";
		dropCollection(collectionName);
		DBCollection collection = db.getCollection(collectionName);
		BulkWriteOperation builder = collection.initializeUnorderedBulkOperation();
		for(DBObject lineIntegration : lineIntegrations){
			builder.insert(lineIntegration);
		}
		builder.execute();
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	private List<String> readFile(String file){
		List<String> lines = new LinkedList<String>();
		BufferedReader reader = null;
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
			reader = new BufferedReader(fileReader);
			String line = null;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Ocorreu um erro ao importar os arquivos", e);
		} catch (IOException e) {
			throw new RuntimeException("Ocorreu um erro ao importar os arquivos", e);
		} finally{
			try {
				if(reader != null){
					reader.close();
				}
				if(fileReader != null){
					fileReader.close();
				}
			} catch (IOException e) {
				throw new RuntimeException("Ocorreu um erro ao importar os arquivos", e);
			}
		}
		return lines;
	}
	
	/**
	 * 
	 * @param keys
	 * @param line
	 * @return
	 */
	private DBObject getDocumentFromCSVLine(String[] keys, String line){
		BasicDBObject doc = new BasicDBObject();
		line = line.replace("\"", "");
		String[] values = line.split(CSV_DELIMITER);
		for(int i = 0; i < keys.length; i++){
			String key = keys[i];
			Object value = values[i];
			
			if(DOUBLE_FIELDS.contains(key)){
				value = Double.valueOf(value.toString());
			} else if (INT_FIELDS.contains(key)){
				value = Integer.valueOf(value.toString());
			}
			doc.append(key, value);
		}
		return doc;
	}
	
	private DB getDB(){
		if(this.db == null){
			try {
				MongoClient client = new MongoClient(RESOURCE.getString("db.url"), Integer.valueOf(RESOURCE.getString("db.port")));
				db = client.getDB(RESOURCE.getString("db.name"));
			} catch (NumberFormatException e) {
				throw new RuntimeException("Parametros inválidos para a conexão com o banco", e);
			} catch (UnknownHostException e) {
				throw new RuntimeException("Parametros inválidos para a conexão com o banco", e);
			}
		}
		return this.db;
	}
	
	private String getCollectionName(String file){
		String[] parts = file.split("/");
		String fileName = parts[parts.length -1];
		return fileName.substring(0, fileName.indexOf("."));
	}

}
