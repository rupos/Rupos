package test.importer;

import java.io.InputStream;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Token;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.importing.Importer;
import org.processmining.mining.petrinetmining.PetriNetResult;

import att.grappa.Edge;

public class HelloImport {
	
	@Importer(name="Guancio Importer", extension="pdf")
	public static PetriNetResult importNet(InputStream input) {
		PetriNet net = new PetriNet();
		Place start = new Place("Strat", net);
		Place end = new Place("Strat", net);
		
		start.addToken(new Token());
		
		Transition action1 = new Transition("action1", net);
		Transition action2 = new Transition("action2", net);

		net.addPlace(start);
		net.addTransition(action1);
		net.addTransition(action2);
		net.addPlace(end);

		net.addEdge(start, action1);
		net.addEdge(start, action2);
		net.addEdge(action1, end);
		net.addEdge(action2, end);
		
		Place repeat1 = new Place("repeat1", net);
		net.addPlace(repeat1);
		Place repeat2 = new Place("repeat2", net);
		net.addPlace(repeat2);

		net.addEdge(action1, repeat1);
		net.addEdge(action2, repeat2);
		net.addEdge(repeat1, action1);
		net.addEdge(repeat2, action2);

		
		return new PetriNetResult(net);
	}
//	@Importer(name="Guancio Importer", extension="pdf")
//	public static PetriNetResult importNet(InputStream input) {
//		PetriNet net = new PetriNet();
//		Place start = new Place("place_1", net);
//		Transition register_complete = new Transition("RegisterUser (complete)", net);
//		Place place5 = new Place("place_5", net);
//		Transition draft_start = new Transition("Draft (start)", net);
//		Place place8 = new Place("place_8", net);
//		Transition draft_complete = new Transition("Draft (complete)", net);
//		
//		
//		Place end = new Place("place_0", net);
//		
//		start.addToken(new Token());
//
//		net.addPlace(start);
//		net.addPlace(place5);
//		net.addPlace(place8);
//		net.addPlace(end);
//
//		net.addTransition(register_complete);
//		net.addTransition(draft_start);
//		net.addTransition(draft_complete);
//		
//
//		net.addEdge(start, register_complete);
//		net.addEdge(register_complete, place5);
//		net.addEdge(place5, draft_start);
//		net.addEdge(draft_start, place8);
//		net.addEdge(place8, draft_complete);
//		net.addEdge(draft_complete, draft_complete);
//		
//		Place repeat1 = new Place("repeat1", net);
//		net.addPlace(repeat1);
//		Place repeat2 = new Place("repeat2", net);
//		net.addPlace(repeat2);
//
//		net.addEdge(action1, repeat1);
//		net.addEdge(action2, repeat2);
//		net.addEdge(repeat1, action1);
//		net.addEdge(repeat2, action2);
//
//		
//		return new PetriNetResult(net);
//	}
}
