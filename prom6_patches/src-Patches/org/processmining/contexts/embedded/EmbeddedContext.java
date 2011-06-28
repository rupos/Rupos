package org.processmining.contexts.embedded;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.impl.AbstractGlobalContext;

public class EmbeddedContext extends AbstractGlobalContext {

	private final EmbeddedPluginContext mainPluginContext;

	public EmbeddedContext() {
		super();

		mainPluginContext = new EmbeddedPluginContext(this, "Main Plugin Context");
	}

	@Override
	protected EmbeddedPluginContext getMainPluginContext() {
		return mainPluginContext;
	}

	@Override
	public Class<? extends PluginContext> getPluginContextType() {
		return EmbeddedPluginContext.class;
	}

}
