package it.unipi.rupos.processmining;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.contexts.cli.ProMManager;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessSetting;
import org.processmining.plugins.petrinet.replayfitness.TotalPerformanceResult;

import org.processmining.plugins.petrinet.replayfitness.TotalFitnessResult;

import java.util.concurrent.CancellationException;
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

    public TotalFitnessResult getFitness(XLog log, ReplayFitnessSetting settings)  throws ExecutionException,InterruptedException {
	return manager.getFitness(this.net, log, settings);
    }

    public TotalFitnessResult getFitness(XTrace trace, ReplayFitnessSetting settings)  throws ExecutionException,InterruptedException {
	XLog log = new XLogImpl(new XAttributeMapImpl());
	log.add(trace);
	return manager.getFitness(this.net, log, settings);
    }

	public TotalPerformanceResult getPerformance(XLog log,
			ReplayFitnessSetting settings) throws CancellationException, ExecutionException, InterruptedException {
		return manager.getPerformance(this.net, log, settings);
	}
}