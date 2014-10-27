package com.brasilct;

import com.brasilct.model.ImportDB;
import com.brasilct.model.mongo.MongoImportDB;

/**
 * 
 * @author mauro
 *
 */
public class Main {

	public static void main(String[] args) {
		ImportDB importDB = new MongoImportDB();
		System.out.println("Loading collections...");
		importDB.load(args);
		System.out.println("Creating Integrations...");
		importDB.createLinesIntegration();
		System.out.println("Done");
	}

}
