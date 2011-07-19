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


/**
 * @author Dipartimento di Informatica - Rupos
 *
 */
public class PetriNetEngine {
    ProMManager manager = null;
    Petrinet net = null;
    Marking marking = null;
    public PetriNetEngine(ProMManager manager, Petrinet net, Marking marking) {
	this.net = net;
	this.marking = marking;
	this.manager = manager;
    }

    /**Suggerisce i settings per il replay del log passato per parametro
     * @param log
     * @return i settaggi suggeriti
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ReplayFitnessSetting suggestSettings(XLog log) throws ExecutionException,InterruptedException {
	return manager.suggestSettings(this.net, log);
    }

    /**Restituisce i dati di Fitness per un log  dati i settaggi per il replay della rete
     * @param log
     * @param settings
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public TotalFitnessResult getFitness(XLog log, ReplayFitnessSetting settings)  throws ExecutionException,InterruptedException {
	return manager.getFitness(this.net, log, settings);
    }

    /**Restituisce i dati di Fitness per una traccia  dati i settaggi per il replay della rete
     * @param trace
     * @param settings
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public TotalFitnessResult getFitness(XTrace trace, ReplayFitnessSetting settings)  throws ExecutionException,InterruptedException {
	XLog log = new XLogImpl(new XAttributeMapImpl());
	log.add(trace);
	return manager.getFitness(this.net, log, settings);
    }

    /**Restituisce i dati di Performance per un log  dati i settaggi per il replay della rete
     * @param log
     * @param settings
     * @return
     * @throws CancellationException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public TotalPerformanceResult getPerformance(XLog log,
			ReplayFitnessSetting settings) throws CancellationException, ExecutionException, InterruptedException {
		return manager.getPerformance(this.net, log, settings);
	}
    /**Restituisce i dati di Performance per una traccia  dati i settaggi per il replay della rete
     * @param trace
     * @param settings
     * @return
     * @throws CancellationException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public TotalPerformanceResult getPerformance(XTrace trace,
			ReplayFitnessSetting settings) throws CancellationException, ExecutionException, InterruptedException {
    	XLog log = new XLogImpl(new XAttributeMapImpl());
    	log.add(trace);
	return manager.getPerformance(this.net, log, settings);
    }
}