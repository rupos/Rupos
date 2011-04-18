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


import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitness;
import org.processmining.plugins.petrinet.replay.ReplayAction;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessSetting;
import org.processmining.plugins.petrinet.replayfitness.TotalFitnessResult;
import org.processmining.plugins.petrinet.replayfitness.TotalPerformanceResult;

import org.processmining.contexts.cli.CLIPluginContext;
import org.processmining.contexts.cli.CLIContext;

public class ProMFactory {
    static public PluginDescriptor openLogPlugin = null;
    static PluginDescriptor alphaPlugin = null;
    static PluginDescriptor fitnessPlugin = null;
    static PluginDescriptor importNetPlugin = null;
    static PluginDescriptor performancePlugin = null;

    public ProMManager createManager() throws Exception {
	    Boot.VERBOSE = Level.NONE;
	    Object res = Boot.boot(ProMManager.class, CLIPluginContext.class, new String[]{});
	    System.out.println("End Initializazion");
	    return (ProMManager)res;
    }
}