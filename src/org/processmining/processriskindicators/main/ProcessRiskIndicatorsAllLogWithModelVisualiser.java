package org.processmining.processriskindicators.main;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

import com.fluxicon.slickerbox.components.HeaderBar;

public class ProcessRiskIndicatorsAllLogWithModelVisualiser {
		@Plugin(
			name = "Process Risk Indicators - with process model", 
			parameterLabels = { "Event log", "Process model"}, 
			returnLabels = {"PRI Analysis Result"}, 
			returnTypes = {HeaderBar.class}, 
			userAccessible = true, 
			help = ""
		)
		@Visualizer
	    public HeaderBar PRIVisualizer(PluginContext context, HeaderBar result) {
	        return result;
	    }
}