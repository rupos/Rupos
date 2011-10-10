package org.processmining.contexts.cli;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.io.File;
import java.lang.InterruptedException;

import java.lang.Thread;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginDescriptor;


import org.processmining.framework.plugin.annotations.Bootable;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.util.CommandLineArgumentList;
import org.processmining.framework.plugin.PluginExecutionResult;


import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramExt;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XLogImpl;

import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessSetting;


import org.processmining.plugins.petrinet.replayfitness.conformance.ConformanceResult;
import org.processmining.plugins.petrinet.replayfitness.conformance.TotalConformanceResult;
import org.processmining.plugins.petrinet.replayfitness.performance.PerformanceResult;
import org.processmining.plugins.petrinet.replayfitness.performance.TotalPerformanceResult;

import org.processmining.contexts.cli.CLIContext;

import it.unipi.rupos.processmining.PetriNetEngine;

/**
 * @author Dipartimento di Informatica - Rupos
 * 
 */
public class ProMManager {
	PluginDescriptor openLogPlugin = null;
	PluginDescriptor alphaPlugin = null;
	PluginDescriptor conformancePlugin = null;
	PluginDescriptor conformancePluginMarki = null;
	PluginDescriptor importNetPlugin = null;
	PluginDescriptor performancePlugin = null;
	PluginDescriptor suggestPlugin = null;
	PluginDescriptor OpenBpmnPlugin = null;
	PluginDescriptor BpmnPlugin = null;
	PluginDescriptor performancewithMarkingPlugin= null;
	PluginDescriptor BPMNMeasureswithAnalisysDetails= null;
	
	CLIContext globalContext = null;
	PluginContext context = null;
	private PluginDescriptor BPMNexport = null;

	@Plugin(name = "ProMManager", parameterLabels = {}, returnLabels = {}, returnTypes = {}, userAccessible = false)
	@Bootable
	public Object main(CommandLineArgumentList commandlineArguments) {
		globalContext = new CLIContext();
		context = globalContext.getMainPluginContext();

		System.out.println("------------------------------");
		for (PluginDescriptor plugin : context.getPluginManager()
				.getAllPlugins()) {
			if ("Open XES Log File".equals(plugin.getName()))
				openLogPlugin = plugin;
			else if ("Alpha Miner".equals(plugin.getName()))
				alphaPlugin = plugin;
			else if ("ConformanceDetailsSettings".equals(plugin.getName()))
				conformancePlugin = plugin;
			else if ("PerformanceDetailsSettings".equals(plugin.getName()))
				performancePlugin = plugin;
			else if ("Import Petri net from PNML file".equals(plugin.getName()))
				importNetPlugin = plugin;
			else if ("FitnessSuggestSettings".equals(plugin.getName()))
				suggestPlugin = plugin;//Import BPMN model from XPDL 2.1 file
			else if ("BPMN to PetriNet".equals(plugin.getName()))
				BpmnPlugin = plugin;
			else if ("Import BPMN model from XPDL 2.1 file".equals(plugin.getName()))
				OpenBpmnPlugin = plugin;
			else if ("ConformanceDetailsSettingsWithMarking".equals(plugin.getName()))
				conformancePluginMarki = plugin;
			else if ("PerformanceDetailsSettingsWithMarking".equals(plugin.getName()))
				performancewithMarkingPlugin = plugin;
			else if ("BPMNMeasureswithAnalisysDetails".equals(plugin.getName()))
				BPMNMeasureswithAnalisysDetails = plugin;
			else if ("XPDL export (Bussines Notation with Artifact)".equals(plugin.getName()))
				BPMNexport  = plugin;//
			else
				continue;
			if (false) {
				System.out.println(plugin.getName());
				for (int j = 0; j < plugin.getParameterTypes().size(); j++) {
					System.out.println("  " + j + " ) "
							+ plugin.getMethodLabel(j) + " "
							+ plugin.getParameterTypes(j));
				}
			}
		}
		try {
			Thread.sleep(1 * 1000);
		} catch (java.lang.InterruptedException e) {
		}

		if (openLogPlugin == null) {
			System.out.println("Plugin OpenLog not found");
		}
		if (alphaPlugin == null) {
			System.out.println("Plugin Alpha not found");
		}
		if (conformancePlugin == null) {
			System.out.println("Plugin Conformance not found");
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
		if (BpmnPlugin == null) {
			System.out.println("Plugin BpmntopnPlugin not found");
		}
		if (conformancePluginMarki == null) {
			System.out.println("Plugin fitness with marking not found");
		}
		if (performancewithMarkingPlugin == null) {
			System.out.println("Plugin performance with marking not found");
		}
		if (BPMNMeasureswithAnalisysDetails == null) {
			System.out.println("Plugin BPMNMeasureswithAnalisysDetails not found");
		}
		if (OpenBpmnPlugin == null) {
			System.out.println("Plugin import bpmn not found");
		}
		if (BPMNexport == null) {
			System.out.println("XPDL export (Bussines Notation with Artifact) not found");
		}
		context = context.createChildContext("MainContext");

		System.out.println("End Initializazion 1");
		return this;
	}

	/**
	 * @param petriNetFile
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public PetriNetEngine createPetriNetEngine(String petriNetFile)
	throws ExecutionException, InterruptedException {
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
		//GraphLayoutConnection layout = res.getResult(2);
		PetriNetEngine res1 = new PetriNetEngine(this, net, startMarking);
		context1.getParentContext().deleteChild(context1);
		return res1;
	}

	/**
	 * @param logFile
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public XLog openLog(String logFile) throws ExecutionException,
	InterruptedException {
		System.out.println("------------------------------");
		System.out.println("Open Log");
		System.out.println("------------------------------");
		PluginContext context1 = context
		.createChildContext("Result of Import Log Error");
		openLogPlugin.invoke(0, context1, logFile);
		context1.getResult().synchronize();
		XLog res = (XLog) context1.getResult().getResult(0);
		context1.getParentContext().deleteChild(context1);
		return res;
	}

	/**
	 * @param net
	 * @param log
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public ReplayFitnessSetting suggestSettings(Petrinet net, XLog log)
	throws ExecutionException, InterruptedException {
		System.out.println("------------------------------");
		System.out.println("Suggest settings");
		System.out.println("------------------------------");
		PluginContext context1 = context.createChildContext("Result of suggest settings");
		suggestPlugin.invoke(0, context1, log, net);
		context1.getResult().synchronize();
		ReplayFitnessSetting res = (ReplayFitnessSetting) context1.getResult()
		.getResult(0);
		context1.getParentContext().deleteChild(context1);
		return res;
	}

	/**
	 * @param net
	 * @param log
	 * @param settings
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public TotalConformanceResult getConformance(Petrinet net, XLog log,
			ReplayFitnessSetting settings) throws ExecutionException,
			InterruptedException {
		System.out.println("------------------------------");
		System.out.println("Conformance Details");
		System.out.println("------------------------------");
		PluginContext context1 = context.createChildContext("Conformance Checking");

		conformancePlugin.invoke(0, context1, log, net, settings);
		context1.getResult().synchronize();
		System.out.println("------------------------------");
		PluginExecutionResult res2 = context1.getResult();
		System.out.println("Obtained " + res2.getSize() + " results");
		System.out.println("------------------------------");
		TotalConformanceResult fitness = res2.getResult(0);
		System.out.println("------------------------------");

		context1.getParentContext().deleteChild(context1);
		return fitness;
	}
	
	public TotalConformanceResult getConformance(Petrinet net, XLog log,
			ReplayFitnessSetting settings, Marking marking) throws ExecutionException,
			InterruptedException {
		System.out.println("------------------------------");
		System.out.println("Conformance Details");
		System.out.println("------------------------------");
		PluginContext context1 = context.createChildContext("Conformance Checking");

		conformancePluginMarki.invoke(0, context1, log, net, settings,marking);
		context1.getResult().synchronize();
		System.out.println("------------------------------");
		PluginExecutionResult res2 = context1.getResult();
		System.out.println("Obtained " + res2.getSize() + " results");
		System.out.println("------------------------------");
		TotalConformanceResult fitness = res2.getResult(0);
		System.out.println("------------------------------");

		context1.getParentContext().deleteChild(context1);
		return fitness;
	}
	/**
	 * @param net
	 * @param log
	 * @param settings
	 * @return
	 * @throws CancellationException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public TotalPerformanceResult getPerformance(Petrinet net, XLog log,
			ReplayFitnessSetting settings) throws CancellationException,
			ExecutionException, InterruptedException {
		System.out.println("------------------------------");
		System.out.println("Performance Details");
		System.out.println("------------------------------");
		PluginContext context1 = context
		.createChildContext("Performance Checking");

		performancePlugin.invoke(0, context1, log, net, settings);
		context1.getResult().synchronize();
		System.out.println("------------------------------");
		PluginExecutionResult res2 = context1.getResult();
		System.out.println("Obtained " + res2.getSize() + " results");
		System.out.println("------------------------------");
		TotalPerformanceResult performance = res2.getResult(0);
		System.out.println("------------------------------");

		context1.getParentContext().deleteChild(context1);
		return performance;
	}

	public TotalPerformanceResult getPerformance(Petrinet net, XTrace trace,
			ReplayFitnessSetting settings, Marking marking) throws CancellationException,
			ExecutionException, InterruptedException {
		System.out.println("------------------------------");
		System.out.println("Performance Details");
		System.out.println("------------------------------");
		PluginContext context1 = context
		.createChildContext("Performance Checking");
		 XLog log = new XLogImpl(new XAttributeMapImpl());
	 	 log.add(trace);

		performancewithMarkingPlugin.invoke(0, context1, log, net, settings, marking);
		context1.getResult().synchronize();
		System.out.println("------------------------------");
		PluginExecutionResult res2 = context1.getResult();
		System.out.println("Obtained " + res2.getSize() + " results");
		System.out.println("------------------------------");
		TotalPerformanceResult performance = res2.getResult(0);
		System.out.println("------------------------------");

		context1.getParentContext().deleteChild(context1);
		return performance;
	}
	public TotalPerformanceResult getPerformance(Petrinet net, XLog log,
			ReplayFitnessSetting settings, Marking marking) throws CancellationException,
			ExecutionException, InterruptedException {
		System.out.println("------------------------------");
		System.out.println("Performance Details");
		System.out.println("------------------------------");
		PluginContext context1 = context
		.createChildContext("Performance Checking");
		

		performancewithMarkingPlugin.invoke(0, context1, log, net, settings, marking);
		context1.getResult().synchronize();
		System.out.println("------------------------------");
		PluginExecutionResult res2 = context1.getResult();
		System.out.println("Obtained " + res2.getSize() + " results");
		System.out.println("------------------------------");
		TotalPerformanceResult performance = res2.getResult(0);
		System.out.println("------------------------------");

		context1.getParentContext().deleteChild(context1);
		return performance;
	}
	public TotalPerformanceResult getPerformance(Petrinet net, XTrace trace,
			ReplayFitnessSetting settings) throws CancellationException,
			ExecutionException, InterruptedException {
		System.out.println("------------------------------");
		System.out.println("Performance Details");
		System.out.println("------------------------------");
		PluginContext context1 = context
		.createChildContext("Performance Checking");
		 XLog log = new XLogImpl(new XAttributeMapImpl());
	 	 log.add(trace);

	 	performancePlugin.invoke(0, context1, log, net, settings);
		context1.getResult().synchronize();
		System.out.println("------------------------------");
		PluginExecutionResult res2 = context1.getResult();
		System.out.println("Obtained " + res2.getSize() + " results");
		System.out.println("------------------------------");
		TotalPerformanceResult performance = res2.getResult(0);
		System.out.println("------------------------------");

		context1.getParentContext().deleteChild(context1);
		return performance;
	}
	public PetriNetEngine getBpmntoPn(BPMNDiagram bpmn) throws CancellationException, ExecutionException, InterruptedException{
		System.out.println("------------------------------");
		System.out.println("Convert BPMN to Petri Nets");
		System.out.println("------------------------------");
		PluginContext context1 = context.createChildContext("Result of Trasform");
		BpmnPlugin.invoke(0, context1, bpmn);
		context1.getResult().synchronize();
		Petrinet res = (Petrinet) context1.getResult().getResult(0);
		Marking marking= (Marking) context1.getResult().getResult(1);
		PetriNetEngine res1 = new PetriNetEngine(this, res, marking);
		context1.getParentContext().deleteChild(context1);
		return res1;
	}
	//BPMNMeasureswithAnalisysDetails
	public BPMNDiagramExt getBPMNwithAnalysis(TotalConformanceResult tcr) throws CancellationException, ExecutionException, InterruptedException{
		System.out.println("------------------------------");
		System.out.println("BPMN with conformance analysis ");
		System.out.println("------------------------------");
		PluginContext context1 = context.createChildContext("Calc Conformance");
		BPMNMeasureswithAnalisysDetails.invoke(0, context1, tcr);
		context1.getResult().synchronize();
		BPMNDiagramExt res = (BPMNDiagramExt) context1.getResult().getResult(0);
		context1.getParentContext().deleteChild(context1);
		return res;
	}
	
	public BPMNDiagramExt getBPMNwithAnalysis(Petrinet pn , ConformanceResult tcr) throws CancellationException, ExecutionException, InterruptedException{
		System.out.println("------------------------------");
		System.out.println("BPMN with conformance analysis ");
		System.out.println("------------------------------");
		PluginContext context1 = context.createChildContext("Calc Conformance");
		BPMNMeasureswithAnalisysDetails.invoke(0, context1, pn,tcr);
		context1.getResult().synchronize();
		BPMNDiagramExt res = (BPMNDiagramExt) context1.getResult().getResult(0);
		context1.getParentContext().deleteChild(context1);
		return res;
	}
	public BPMNDiagramExt getBPMNwithAnalysis(TotalPerformanceResult tcr) throws CancellationException, ExecutionException, InterruptedException{
		System.out.println("------------------------------");
		System.out.println("BPMN with performance analysis ");
		System.out.println("------------------------------");
		PluginContext context1 = context.createChildContext("Calc Performance");
		BPMNMeasureswithAnalisysDetails.invoke(2, context1, tcr);
		context1.getResult().synchronize();
		BPMNDiagramExt res = (BPMNDiagramExt) context1.getResult().getResult(0);
		context1.getParentContext().deleteChild(context1);
		return res;
	}
	
	public BPMNDiagramExt getBPMNwithAnalysis(Petrinet pn , PerformanceResult tcr) throws CancellationException, ExecutionException, InterruptedException{
		System.out.println("------------------------------");
		System.out.println("BPMN with performance analysis ");
		System.out.println("------------------------------");
		PluginContext context1 = context.createChildContext("Calc Performance");
		BPMNMeasureswithAnalisysDetails.invoke(3, context1, pn,tcr);
		context1.getResult().synchronize();
		BPMNDiagramExt res = (BPMNDiagramExt) context1.getResult().getResult(0);
		context1.getParentContext().deleteChild(context1);
		return res;
	}
	public BPMNDiagram openBpmn(String BpmnFile) throws ExecutionException,
	InterruptedException {
		System.out.println("------------------------------");
		System.out.println("Open BPMN XPDL");
		System.out.println("------------------------------");
		PluginContext context1 = context.createChildContext("Result of Import");
		OpenBpmnPlugin.invoke(0, context1, BpmnFile);
		context1.getResult().synchronize();
		BPMNDiagram res = (BPMNDiagram) context1.getResult().getResult(0);
		context1.getParentContext().deleteChild(context1);
		return res;
	}
	public void writefilebpmn(File BpmnFilename, BPMNDiagramExt bpmn) throws ExecutionException,
	InterruptedException {
		System.out.println("------------------------------");
		System.out.println("Write BPMN esteso to  XPDL");
		System.out.println("------------------------------");
		PluginContext context1 = context.createChildContext("Result of Export");
		BPMNexport.invoke(0, context1, bpmn,BpmnFilename);
		context1.getResult().synchronize();
		context1.getResult().getResult(0);
		context1.getParentContext().deleteChild(context1);
		
	}
	public void closeContext() {
		// for ( PluginContext c:
		// globalContext.getMainPluginContext().getChildContexts()) {
		// globalContext.getMainPluginContext().deleteChild(c);
		// }
	}
	public PluginContext getPluginContext() {
		return this.context;
		// for ( PluginContext c:
		// globalContext.getMainPluginContext().getChildContexts()) {
		// globalContext.getMainPluginContext().deleteChild(c);
		// }
	}

}
