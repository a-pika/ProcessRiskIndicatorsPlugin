package org.processmining.processriskindicators.pri;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeBooleanImpl;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.processmining.contexts.uitopia.UIPluginContext;


public class AtypicalActivity {

		public static XLog getRiskyTwoLogs (UIPluginContext context, XLog trainLog, XLog testLog, String act_name, Double threshold){
			
			int numTrace = trainLog.size();
			int numActExec = 0;
					
			
			for (XTrace t : trainLog) {
															 
			for (XEvent e : t) 
				{
				XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
				String lifecycle = XLifecycleExtension.instance().extractTransition(event);
				String eventName = XConceptExtension.instance().extractName(event);
				
				if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
				{
					numActExec+=1;
				}
				}
								
			}
			
			Double Actpercentage = (double)numActExec/numTrace;
			context.log("act %: "+Actpercentage.toString());
			context.log("thres: "+threshold.toString());
			
			XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(testLog.getAttributes());
				
						
						for (XTrace t : testLog) {
							XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
															 
						for (XEvent e : t) 
							{
							XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
							String eventName = XConceptExtension.instance().extractName(event);
							String lifecycle = XLifecycleExtension.instance().extractTransition(event);
							
							if (lifecycle.equalsIgnoreCase("complete")  && eventName.equals(act_name) && Actpercentage < threshold)
							{
									
								XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
								long duration = a_duration.getValue();
								if (duration>-1){
									event.getAttributes().put("feature:odd_activity",new XAttributeBooleanImpl("feature:odd_activity",true));
									event.getAttributes().put("feature:odd_activity_duration",new XAttributeContinuousImpl("feature:odd_activity_duration",duration));
								}
								
							}
								trace.add(event);
							}
						copylog.add(trace);				
						}
			return copylog;
			
		};
	
}

//----------------------------------------------PREV. VERSIONS------------------------------------------------------
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*	

//-----------------------------------annotate all risk--------------------------------------------------	
			public static XLog Risky (UIPluginContext context, XLog trainLog, XLog testLog, String act_name, Double threshold){
				
				int numTrace = trainLog.size();
				int numActExec = 0;
						
				
				for (XTrace t : trainLog) {
																 
				for (XEvent e : t) 
					{
					XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
					String lifecycle = XLifecycleExtension.instance().extractTransition(event);
					String eventName = XConceptExtension.instance().extractName(event);
					
					if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
					{
						numActExec+=1;
					}
					}
									
				}
				
				Double Actpercentage = (double)numActExec/numTrace;
				context.log("act %: "+Actpercentage.toString());
				context.log("thres: "+threshold.toString());
				
				XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(testLog.getAttributes());
					
							
							for (XTrace t : testLog) {
								XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
																 
							for (XEvent e : t) 
								{
								XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
								String eventName = XConceptExtension.instance().extractName(event);
								String lifecycle = XLifecycleExtension.instance().extractTransition(event);
								
								if (lifecycle.equalsIgnoreCase("complete")  && eventName.equals(act_name))
								{
																		
																	
									event.getAttributes().put("PRI4:part",new XAttributeContinuousImpl("PRI4:part",Actpercentage));
									event.getAttributes().put("PRI4:times",new XAttributeContinuousImpl("PRI4:times",Actpercentage/threshold));
																
								}
									trace.add(event);
								}
							copylog.add(trace);				
							}
				return copylog;
				
			};
	

public static XLog getRiskySplit (UIPluginContext context, XLog log, String act_name, Double threshold){
		
		
	int i = 0;
	int splitIndex = 0;
	int size = log.size();
	
	if( (size % 2) == 0){splitIndex = size/2;}else{splitIndex = (size+1)/2;}	
//---------------------------------------------------------------------------
	
	//int numTrace = log.size();
	int numTrace = 0;
	
		int numActExec = 0;
				
		
		for (XTrace t : log) {
		if (i<splitIndex){												 
		for (XEvent e : t) 
			{
			XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
			String lifecycle = XLifecycleExtension.instance().extractTransition(event);
			String eventName = XConceptExtension.instance().extractName(event);
			
			if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
			{
				numActExec+=1;
			}
			}
		numTrace++;}i++;				
		}
		
		Double Actpercentage = (double)numActExec/numTrace;
		context.log("act %: "+Actpercentage.toString());
		context.log("thres: "+threshold.toString());
		
		XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
			
					i=0;
					for (XTrace t : log) {
						XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
					
						//---------------------case beginning time----------------------------------------
						XEvent first = t.get(0);
						Date begin = XTimeExtension.instance().extractTimestamp(first);
						
						//--------------------------------------------------------------------------------							
			
						
						if (i<splitIndex)
						{
							trace.getAttributes().put("outcome:case_duration",new XAttributeBooleanImpl("outcome:case_duration",false));
							trace.getAttributes().put("outcome:SLA_violation",new XAttributeContinuousImpl("outcome:SLA_violation",0.00));
				
						}								 
					for (XEvent e : t) 
						{
						XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
						if (i>=splitIndex){
						String eventName = XConceptExtension.instance().extractName(event);
						String lifecycle = XLifecycleExtension.instance().extractTransition(event);
						
						if (lifecycle.equalsIgnoreCase("complete")  && eventName.equals(act_name) && Actpercentage < threshold)
						{
							//Date eventTime = XTimeExtension.instance().extractTimestamp(event);
							//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,e));
							//long duration = eventTime.getTime()-matchEventTime.getTime();
							//double dur = duration/60000;
							
							XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
							//XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("subprocess:duration");
							
							long duration = a_duration.getValue();
							//if (duration > 36000000){
							if (duration > 0){
								
								//---------------updating discovery time-----------------------------------------
								String current_discovered = t.getAttributes().get("time:PRI4").toString();
								Long current = Long.parseLong(current_discovered);
								Date end = XTimeExtension.instance().extractTimestamp(event);
								long discovered = end.getTime()-begin.getTime();
								
								if(current > discovered || current_discovered.equals("0"))
							
							{
								
								trace.getAttributes().put("time:PRI4",new XAttributeDiscreteImpl("time:PRI4",discovered));
							}
								//--------------------------------------------------------------------------------
					
								
								event.getAttributes().put("feature:odd_activity",new XAttributeBooleanImpl("feature:odd_activity",true));
								event.getAttributes().put("feature:odd_activity_duration",new XAttributeContinuousImpl("feature:odd_activity_duration",duration));
							}
							
						}
						}
							trace.add(event);
						}i++;
					copylog.add(trace);				
					}
		return copylog;
		
	};	

//-------------------------------------------------------------------------------------	
	public static XLog getRisky (UIPluginContext context, XLog log, String act_name, Double threshold){
		
		int numTrace = log.size();
		int numActExec = 0;
				
		
		for (XTrace t : log) {
														 
		for (XEvent e : t) 
			{
			XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
			String lifecycle = XLifecycleExtension.instance().extractTransition(event);
			String eventName = XConceptExtension.instance().extractName(event);
			
			if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
			{
				numActExec+=1;
			}
			}
							
		}
		
		Double Actpercentage = (double)numActExec/numTrace;
		context.log("act %: "+Actpercentage.toString());
		context.log("thres: "+threshold.toString());
		
		XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
			
					
					for (XTrace t : log) {
						XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
														 
					for (XEvent e : t) 
						{
						XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
						String eventName = XConceptExtension.instance().extractName(event);
						String lifecycle = XLifecycleExtension.instance().extractTransition(event);
						
						if (lifecycle.equalsIgnoreCase("complete")  && eventName.equals(act_name) && Actpercentage < threshold)
						{
							//Date eventTime = XTimeExtension.instance().extractTimestamp(event);
							//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,e));
							//long duration = eventTime.getTime()-matchEventTime.getTime();
							//double dur = duration/60000;
							
							XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
							long duration = a_duration.getValue();
							if (duration!=0){
								event.getAttributes().put("feature:odd_activity",new XAttributeBooleanImpl("feature:odd_activity",true));
								event.getAttributes().put("feature:odd_activity_duration",new XAttributeContinuousImpl("feature:odd_activity_duration",duration));
							}
							
						}
							trace.add(event);
						}
					copylog.add(trace);				
					}
		return copylog;
		
	};

*/