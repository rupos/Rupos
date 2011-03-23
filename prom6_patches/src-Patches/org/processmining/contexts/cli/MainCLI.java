package org.processmining.contexts.cli;

import jargs.gnu.CmdLineParser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginDescriptor;
import org.processmining.framework.boot.Boot;
import org.processmining.framework.boot.Boot.Level;
import org.processmining.framework.plugin.annotations.Bootable;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.util.CommandLineArgumentList;
import org.processmining.framework.plugin.PluginExecutionResult;


import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitness;


public class MainCLI {
	@Plugin(name = "MainCLI", parameterLabels = {}, returnLabels = {}, returnTypes = {}, userAccessible = false)
	@Bootable
	public Object main(CommandLineArgumentList commandlineArguments) {
		try {
		    boolean printPluginSignatures = true;

		    CLIContext globalContext = new CLIContext();
		    PluginContext context = globalContext.getMainPluginContext();
		    PluginDescriptor openLogPlugin = null;
		    PluginDescriptor alphaPlugin = null;
		    PluginDescriptor fitnessPlugin = null;
		    PluginDescriptor importNetPlugin = null;

		    System.out.println("------------------------------");
		    for (PluginDescriptor plugin : context.getPluginManager().getAllPlugins()) {
			if ("Open XES Log File".equals(plugin.getName()))
			    openLogPlugin = plugin;
			else if ("Alpha Miner".equals(plugin.getName()))
			    alphaPlugin = plugin;
			else if ("FitnessDetails".equals(plugin.getName()))
			    fitnessPlugin = plugin;
			else if ("Import Petri net from PNML file".equals(plugin.getName()))
			    importNetPlugin = plugin;
			else
			    continue;
			if (printPluginSignatures) {
			    System.out.println(plugin.getName());
			    for (int j = 0; j < plugin.getParameterTypes().size(); j++) {
				System.out.println("  " + j + " ) " + plugin.getMethodLabel(j) + " " + plugin.getParameterTypes(j));
			    }
			}
		    }

		    PluginContext context1 = null;

		    // System.out.println("------------------------------");
		    // System.out.println(openLogPlugin);
		    // System.out.println("------------------------------");
		    // context1 = context.createChildContext("Result of Import Log");
		    // openLogPlugin.invoke(0, context1, "../prom5_log_files/sequence.mxml");
		    // context1.getResult().synchronize();
		    // org.deckfour.xes.model.XLog res = (org.deckfour.xes.model.XLog)context1.getResult().getResult(0);
		    // System.out.println("------------------------------");
		    // // System.out.println(res);
		    // System.out.println("------------------------------");

		    // System.out.println("------------------------------");
		    // System.out.println(alphaPlugin);
		    // System.out.println("------------------------------");
		    // context1 = context.createChildContext("Result of Alpha");
		    // alphaPlugin.invoke(0, context1, res);
		    // context1.getResult().synchronize();
		    // Object res1 = context1.getResult().getResult(0);
		    // System.out.println("------------------------------");
		    // // System.out.println(res1);
		    // System.out.println("------------------------------");

		    System.out.println("------------------------------");
		    System.out.println(importNetPlugin);
		    System.out.println("------------------------------");
		    context1 = context.createChildContext("Import Net");
		    importNetPlugin.invoke(0, context1, "../prom5_log_files/sequence_prom6.pnml");
		    context1.getResult().synchronize();
		    System.out.println("------------------------------");
		    PluginExecutionResult res1 = context1.getResult();
		    System.out.println("Obtained " + res1.getSize() + " results");
		    System.out.println("------------------------------");
		    Object net = res1.getResult(0);
		    Marking startMarking = res1.getResult(1);
		    System.out.println("------------------------------");


		    System.out.println("------------------------------");
		    System.out.println(openLogPlugin);
		    System.out.println("------------------------------");
		    context1 = context.createChildContext("Result of Import Log Error");
		    openLogPlugin.invoke(0, context1, "../prom5_log_files/errors.mxml");
		    context1.getResult().synchronize();
		    org.deckfour.xes.model.XLog errors = (org.deckfour.xes.model.XLog)context1.getResult().getResult(0);
		    System.out.println("------------------------------");
		    // System.out.println(res);
		    System.out.println("------------------------------");


		    System.out.println("------------------------------");
		    System.out.println(fitnessPlugin);
		    System.out.println("------------------------------");
		    context1 = context.createChildContext("Fitness Checking");
		    fitnessPlugin.invoke(0, context1, errors, net);
		    context1.getResult().synchronize();
		    System.out.println("------------------------------");
		    PluginExecutionResult res2 = context1.getResult();
		    System.out.println("Obtained " + res2.getSize() + " results");
		    System.out.println("------------------------------");
		    ReplayFitness fitness = res2.getResult(0);
		    System.out.println(fitness.getValue());
		    System.out.println("------------------------------");
		    Marking remaining = res2.getResult(1);
		    System.out.println(remaining);
		    System.out.println("------------------------------");
		    Marking missing = res2.getResult(2);
		    System.out.println(missing);
		    System.out.println("------------------------------");

		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
		return null;
	}

	public static void main(String[] args) throws Exception {
	    Boot.VERBOSE = Level.NONE;
	    Boot.boot(MainCLI.class, CLIPluginContext.class, args);
	}
}
