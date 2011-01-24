import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jcouchdb.db.Database;
import org.jcouchdb.document.BaseDocument;
import org.jcouchdb.document.DesignDocument;
import org.jcouchdb.document.ValueRow;
import org.jcouchdb.document.View;
import org.jcouchdb.document.ViewResult;


public class Main {

	public void addNewDoc(Database db) {
		// create a hash map document with two fields    
	    Map<String,String> doc = new HashMap<String, String>();
	    doc.put("foo", "value for the foo attribute");
	    doc.put("bar", "value for the bar attribute");

	    // create the document in couchdb
	    db.createDocument(doc);
	}
	
	public void addNewDocTwo(Database db) {
		// create a hash map document with two fields
		BaseDocument doc = new BaseDocument();
		doc.setId("Document Ciao");
		doc.setProperty("foo", "Value of first property");
		doc.setProperty("foo", "Value of second property");

	    // create the document in couchdb
	    db.createDocument(doc);
	}

	public void cleanDB(Database db) {
		DesignDocument view = db.getDesignDocument("AllDocuments");
		HashMap<String, View> map = new HashMap<String, View>();
		
		View mapView = new View();
		mapView.setMap("function(doc) {emit(null, doc);}");

		View noSessionView = new View();
		noSessionView.setMap("function(doc) {if (!doc.session) { emit(null, doc._id);} }");

		View sessionView = new View();
		sessionView.setMap("function(doc) {if (doc.session) { emit(doc.session, doc._id);} }");

		View genSessionView = new View();
		genSessionView.setMap("" +
				"function(doc) {" +
					"if (doc.type == 1) { emit([doc.value.key1, null], doc._id);}" +
					"else if (doc.type == 2) { emit([doc.value.key1, doc.value.key2], doc._id);}" +
					"else if (doc.type == 3) { emit([null, doc.value.key2], doc._id);}" +
					" }");

		View toInitView = new View();
		toInitView.setMap("" +
				"function(doc) {" +
					"session = null;" +
					"if (doc.type == 1) { session = [doc.value.key1, null];}" +
					"else if (doc.type == 2) { session = [doc.value.key1, doc.value.key2];}" +
					"else if (doc.type == 3) { session = [null, doc.value.key2];}" +
					"if (!doc.session || doc.session[0] != session[0] || doc.session[1] != session[1]) { emit(session, {id: doc._id, rev: doc._rev}); }" +
					" }");

		map.put("all", mapView);
		map.put("no_session", noSessionView);
		map.put("session", sessionView);
		map.put("gen_session", genSessionView);
		map.put("to_init", toInitView);
		
		view.setViews(map);
		db.updateDocument(view);
		//db.createOrUpdateDocument(view);
		
		ViewResult<Map> res = db.queryView("AllDocuments/all", Map.class, null, null);
		System.out.println("Existing docs: " + res.getTotalRows());
		for (ValueRow<Map> row : res.getRows()) {
			db.delete(row.getId(), (String) row.getValue().get("_rev"));
		}
		
		
		BaseDocument doc = new BaseDocument();
		doc.setId("Document1");
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("key1", 100);
		doc.setProperty("value", values);
		doc.setProperty("type", 1);
		db.createDocument(doc);
		
		doc = new BaseDocument();
		doc.setId("Document2");
		values = new HashMap<String, Object>();
		values.put("key1", 50);
		doc.setProperty("value", values);
		doc.setProperty("type", 1);
		db.createDocument(doc);

		doc = new BaseDocument();
		doc.setId("Document3");
		values = new HashMap<String, Object>();
		values.put("key1", 100);
		values.put("key2", "a");
		doc.setProperty("value", values);
		doc.setProperty("type", 2);
		db.createDocument(doc);

		doc = new BaseDocument();
		doc.setId("Document4");
		values = new HashMap<String, Object>();
		values.put("key1", 50);
		values.put("key2", "b");
		doc.setProperty("value", values);
		doc.setProperty("type", 2);
		db.createDocument(doc);

		doc = new BaseDocument();
		doc.setId("Document5");
		values = new HashMap<String, Object>();
		values.put("key2", "b");
		doc.setProperty("value", values);
		doc.setProperty("type", 3);
		db.createDocument(doc);

		doc = new BaseDocument();
		doc.setId("Document6");
		values = new HashMap<String, Object>();
		values.put("key2", "a");
		doc.setProperty("value", values);
		doc.setProperty("type", 3);
		db.createDocument(doc);

		res = db.queryView("AllDocuments/to_init", Map.class, null, null);
		System.out.println("Found docs: " + res.getTotalRows());
		for (ValueRow<Map> row : res.getRows()) {
			String id = (String) row.getValue().get("id");
			String rev = (String) row.getValue().get("rev");
			List session = (List) row.getKey();
			doc = db.getDocument(BaseDocument.class, id);
			doc.setProperty("session", session);
			db.updateDocument(doc);
		}
}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main();
		
		Database db = new Database("localhost", "example");
		main.cleanDB(db);
	}

}
