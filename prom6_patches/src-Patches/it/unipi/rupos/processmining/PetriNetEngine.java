package it.unipi.rupos.processmining;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.contexts.cli.ProMManager;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessSetting;

import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;


public class PetriNetEngine {
    ProMManager manager = null;
    Petrinet net = null;
    Marking marking = null;
    public PetriNetEngine(ProMManager manager, Petrinet net, Marking marking) {
	this.net = net;
	this.marking = marking;
	this.manager = manager;
    }

    public ReplayFitnessSetting suggestSettings(XLog log) throws ExecutionException,InterruptedException {
	return manager.suggestSettings(this.net, log);
    }
}