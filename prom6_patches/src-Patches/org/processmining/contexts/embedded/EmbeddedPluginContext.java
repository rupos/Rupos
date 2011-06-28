package org.processmining.contexts.embedded;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.processmining.contexts.cli.CLIProgressBar;
import org.processmining.framework.plugin.GlobalContext;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.impl.AbstractPluginContext;

public class EmbeddedPluginContext extends AbstractPluginContext {

	private final Executor executor;

	public EmbeddedPluginContext(GlobalContext context, String label) {
		super(context, label);
		// This context is NOT a child of another context,
		// hence should behave in an asynchronous way.
		executor = new ProMFactory().createExecutor();
		progress = new CLIProgressBar(this);
	}

	protected EmbeddedPluginContext(EmbeddedPluginContext context, String label) {
		super(context, label);
		progress = new CLIProgressBar(this);
		// This context is a child of another context,
		// hence should behave in a synchronous way.
		if (context.getParentContext() == null) {
			// this context is on the first level below the user-initiated
			// plugins
			executor = new ProMFactory().createExecutor();
		} else {
			// all subtasks take the pool of the parent.
			executor = context.getExecutor();
		}
	}

	@Override
	protected synchronized PluginContext createTypedChildContext(String label) {
		return new EmbeddedPluginContext(this, label);
	}

	public Executor getExecutor() {
		return executor;
	}

	@Override
	public Progress getProgress() {
		return progress;
	}

	@Override
	public EmbeddedContext getGlobalContext() {
		return (EmbeddedContext) super.getGlobalContext();
	}

	@Override
	public EmbeddedPluginContext getRootContext() {
		return (EmbeddedPluginContext) super.getRootContext();
	}
}
