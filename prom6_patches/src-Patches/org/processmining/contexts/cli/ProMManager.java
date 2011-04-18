package org.processmining.contexts.cli;

import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;

import java.lang.Thread;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginDescriptor;
import org.processmining.framework.boot.Boot;
import org.processmining.framework.boot.Boot.Level;
import org.processmining.framework.plugin.annotations.Bootable;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.util.CommandLineArgumentList;
import org.processmining.framework.plugin.PluginExecutionResult;


import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessSetting;

import org.processmining.plugins.petrinet.replayfitness.ReplayFitness;
import org.processmining.plugins.petrinet.replay.ReplayAction;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessSetting;
import org.processmining.plugins.petrinet.replayfitness.TotalFitnessResult;
import org.processmining.plugins.petrinet.replayfitness.TotalPerformanceResult;

import org.processmining.contexts.cli.CLIPluginContext;
import org.processmining.contexts.cli.CLIContext;

import it.unipi.rupos.processmining.PetriNetEngine;

public class ProMManager {
    PluginDescriptor openLogPlugin = null;
    PluginDescriptor alphaPlugin = null;
    PluginDescriptor fitnessPlugin = null;
    PluginDescriptor importNetPlugin = null;
    PluginDescriptor performancePlugin = null;
    PluginDescriptor suggestPlugin = null;
    CLIContext globalContext = null;
    PluginContext context = null;

    @Plugin(name = "ProMManager", parameterLabels = {}, returnLabels = {}, returnTypes = {}, userAccessible = false)
    @Bootable
    public Object main(CommandLineArgumentList commandlineArguments) {
	globalContext = new CLIContext();
	context = globalContext.getMainPluginContext();

	System.out.println("------------------------------");
	for (PluginDescriptor plugin : context.getPluginManager().getAllPlugins()) {
	    if ("Open XES Log File".equals(plugin.getName()))
		openLogPlugin = plugin;
	    else if ("Alpha Miner".equals(plugin.getName()))
		alphaPlugin = plugin;
	    else if ("FitnessDetailsSettings".equals(plugin.getName()))
		fitnessPlugin = plugin;
	    else if ("PerformanceDetails".equals(plugin.getName()))
		performancePlugin = plugin;
	    else if ("Import Petri net from PNML file".equals(plugin.getName()))
		importNetPlugin = plugin;
	    else if ("FitnessSuggestSettings".equals(plugin.getName()))
		suggestPlugin = plugin;
	    else
		continue;
	    if (false) {
		System.out.println(plugin.getName());
		for (int j = 0; j < plugin.getParameterTypes().size(); j++) {
		    System.out.println("  " + j + " ) " + plugin.getMethodLabel(j) + " " + plugin.getParameterTypes(j));
		}
	    }
	}
	try {
	    Thread.sleep(1*1000);
	}
	catch (java.lang.InterruptedException e) {
	}

	if (openLogPlugin == null) {
	    System.out.println("Plugin OpenLog not found");
	}
	if (alphaPlugin == null) {
	    System.out.println("Plugin Alpha not found");
	}
	if (fitnessPlugin == null) {
	    System.out.println("Plugin Fitness not found");
	}
	if (importNetPlugin == null) {
	    System.out.println("Plugin ImportNet not found");
	}
	if (performancePlugin == null) {
	    System.out.println("Plugin Performance not found");
	}
	if (suggestPlugin == null) {
	    System.out.println("Plugin SuggestSettings not found");
	}

	System.out.println("End Initializazion 1");
	return this;
    }

    public PetriNetEngine createPetriNetEngine(String petriNetFile) throws ExecutionException,InterruptedException {
	System.out.println("------------------------------");
	System.out.println("Import Net");
	System.out.println("------------------------------");
	PluginContext context1 = context.createChildContext("Import Net");
	importNetPlugin.invoke(0, context1, petriNetFile);
	context1.getResult().synchronize();
	System.out.println("------------------------------");
	PluginExecutionResult res = context1.getResult();
	System.out.println("Obtained " + res.getSize() + " results");
	System.out.println("------------------------------");
	Petrinet net = res.getResult(0);
	Marking startMarking = res.getResult(1);
	System.out.println("------------------------------");

	PetriNetEngine res1 = new PetriNetEngine(this, net, startMarking);
	return res1;
    }

    public XLog openLog(String logFile) throws ExecutionException,InterruptedException {
	System.out.println("------------------------------");
	System.out.println("Open Log");
	System.out.println("------------------------------");
	PluginContext context1 = context.createChildContext("Result of Import Log Error");
	openLogPlugin.invoke(0, context1, logFile);
	context1.getResult().synchronize();
	return (XLog)context1.getResult().getResult(0);
    }

    public ReplayFitnessSetting suggestSettings(Petrinet net, XLog log) throws ExecutionException,InterruptedException {
	System.out.println("------------------------------");
	System.out.println("Suggest settings");
	System.out.println("------------------------------");
	PluginContext context1 = context.createChildContext("Result of suggest settings");
	suggestPlugin.invoke(0, context1, log, net);
	context1.getResult().synchronize();
	return (ReplayFitnessSetting)context1.getResult().getResult(0);
    }

    public TotalFitnessResult getFitness(Petrinet net, XLog log, ReplayFitnessSetting settings)  throws ExecutionException,InterruptedException {
	System.out.println("------------------------------");
	System.out.println("Fitness Details");
	System.out.println("------------------------------");
	PluginContext context1 = context.createChildContext("Fitness Checking");

	fitnessPlugin.invoke(0, context1, log, net, settings);
	context1.getResult().synchronize();
	System.out.println("------------------------------");
	PluginExecutionResult res2 = context1.getResult();
	System.out.println("Obtained " + res2.getSize() + " results");
	System.out.println("------------------------------");
	TotalFitnessResult fitness = res2.getResult(0);
	System.out.println("------------------------------");
	return fitness;
    }

}