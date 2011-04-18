package it.unipi.rupos.processmining;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;

public class PetriNetEngine {
    Petrinet net = null;
    Marking marking = null;
    public PetriNetEngine(Petrinet net, Marking marking) {
	this.net = net;
	this.marking = marking;
    }
}