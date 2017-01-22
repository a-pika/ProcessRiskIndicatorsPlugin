package org.processmining.processriskindicators.pri;

import java.util.Vector;

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
import org.processmining.processriskindicators.analysis.Stat;


public class AbnormalActivityDuration {

	static Stat stat = new Stat();
	
	public static XLog getRiskyLogMedianTwoLogs (UIPluginContext context, XLog trainlog, XLog testlog, String act_name, Double distribution_param, Double stdev_threshold){
		
		
		Vector<Double> durlogs = new Vector<Double>();	
				
		// getting logarithms of durations
		for (XTrace t : trainlog) {
														 
		for (XEvent e : t) 
			{
				
				String eventName = XConceptExtension.instance().extractName(e);
				String lifecycle = XLifecycleExtension.instance().extractTransition(e);
				
				if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
				{
					XAttributeDiscrete a_duration = (XAttributeDiscrete)e.getAttributes().get("time:duration");
					long duration = a_duration.getValue();
					durlogs.add(Math.log10(duration));
				}
			}
							
		}
		
		Double threshold = stat.getMedianThreshold(durlogs, distribution_param, stdev_threshold);
				
		XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(testlog.getAttributes());
			
					
					for (XTrace t : testlog) {
						XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
						copylog.add(trace);
						
											 
					for (XEvent e : t) 
						{
						XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
						
						String eventName = XConceptExtension.instance().extractName(event);
						
						String lifecycle = XLifecycleExtension.instance().extractTransition(event);
						
						if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name)){
							
							XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
							long duration = a_duration.getValue();
							Double logduration = Math.log10(duration);
							
							if (logduration > threshold)
							{
								event.getAttributes().put("feature:act_duration",new XAttributeBooleanImpl("feature:act_duration",true));
								event.getAttributes().put("feature:ab_duration",new XAttributeContinuousImpl("feature:ab_duration",duration-java.lang.Math.pow(10,threshold)));
							
							}
						}
							trace.add(event);
						}
										
					}
		return copylog;
		
	};	
}

//---------------------------------------PREV. VERSIONS---------------------------------------------------------------------------------
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
/*
	//----------------------------------------- get risk and annotate ---------------------------------------------------
	
			public static XLog Risky (UIPluginContext context, XLog trainlog, XLog testlog, String act_name){
				
				Vector<Double> durations = new Vector<Double>();	
						
				for (XTrace t : trainlog) {
																 
				for (XEvent e : t) 
					{
						
						String eventName = XConceptExtension.instance().extractName(e);
						//context.log("act name: "+eventName);
						String lifecycle = XLifecycleExtension.instance().extractTransition(e);
						
						if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
						{
							XAttributeDiscrete a_duration = (XAttributeDiscrete)e.getAttributes().get("time:duration");
							long duration = a_duration.getValue();
							durations.add((double)duration);
						}
					}
									
				}
				//---------------------------------------------------------------------------------------
				Double average = stat.Average(durations);
				Double stDev = stat.getStDev(durations);
				
				System.out.println("task: " + act_name);
				System.out.println("AVG: " + average);
				System.out.println("Std. dev.: " + stDev);
										
				XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(testlog.getAttributes());
					
							
							for (XTrace t : testlog) {
								XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
								copylog.add(trace);
																				 
							for (XEvent e : t) 
								{
								XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
								String eventName = XConceptExtension.instance().extractName(event);
								String lifecycle = XLifecycleExtension.instance().extractTransition(event);
								
								if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name)){
									
																
									XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
									long duration = a_duration.getValue();
									
															
										//event.getAttributes().put("feature:act_duration",new XAttributeBooleanImpl("feature:act_duration",true));
										event.getAttributes().put("PRI1:average",new XAttributeContinuousImpl("PRI1:average",average));
										event.getAttributes().put("PRI1:stDev",new XAttributeContinuousImpl("PRI1:stDev",stDev));
										event.getAttributes().put("PRI1:deviation",new XAttributeContinuousImpl("PRI1:deviation",(duration-average)/stDev));
										System.out.println("PRI1:deviation: " + (duration-average)/stDev);
									
									
								}
									trace.add(event);
								}
												
							}
				return copylog;
				
			};	
	


		
		//-----------------------------------------Logarithm Average with 2 logs---------------------------------------------------------
		
		public static XLog getRiskyLogAverageTwoLogs (UIPluginContext context, XLog trainlog, XLog testlog, String act_name, Double distribution_param, Double stdev_threshold){
					
					//----
					Vector<Double> durlogs = new Vector<Double>();	
							
					// getting logarithms of durations
					for (XTrace t : trainlog) {
																	 
					for (XEvent e : t) 
						{
							
							//Date eventTime = XTimeExtension.instance().extractTimestamp(e);
							//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,e));
							//long duration = eventTime.getTime()-matchEventTime.getTime();
						
							String eventName = XConceptExtension.instance().extractName(e);
							String lifecycle = XLifecycleExtension.instance().extractTransition(e);
							
							if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
							{
								XAttributeDiscrete a_duration = (XAttributeDiscrete)e.getAttributes().get("time:duration");
								long duration = a_duration.getValue();
								durlogs.add(Math.log10(duration));
							}
						}
										
					}
					//----
					//Double threshold = stat.getMedianThreshold(durlogs, distribution_param, stdev_threshold);
					Double threshold = stat.getAverageThreshold(durlogs, stdev_threshold);
							
					XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(testlog.getAttributes());
						
								
								for (XTrace t : testlog) {
									XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
									copylog.add(trace);
									
														 
								for (XEvent e : t) 
									{
									XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
									
										//Date eventTime = XTimeExtension.instance().extractTimestamp(event);
										//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,event));
										//long duration = (eventTime.getTime()-matchEventTime.getTime())/60000;
									String eventName = XConceptExtension.instance().extractName(event);
									
									
									
									String lifecycle = XLifecycleExtension.instance().extractTransition(event);
									
									if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name)){
										
										if (eventName.equals("IP Access"))
										{
											context.log("IP Access: "+threshold.toString());
										}
										
										XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
										long duration = a_duration.getValue();
										Double logduration = Math.log10(duration);
										
										if (logduration > threshold)
										{
											event.getAttributes().put("feature:act_duration",new XAttributeBooleanImpl("feature:act_duration",true));
											event.getAttributes().put("feature:ab_duration",new XAttributeContinuousImpl("feature:ab_duration",duration-java.lang.Math.pow(10,threshold)));
										
										}
									}
										trace.add(event);
									}
													
								}
					return copylog;
					
				};
		
//--------------------------------Median----------------------------------------------------------------------
public static XLog getRiskyMedian (UIPluginContext context, XLog log, String act_name, Double distribution_param, Double stdev_threshold){
		
		//----
		Vector<Double> durlogs = new Vector<Double>();	
				
		// getting logarithms of durations
		for (XTrace t : log) {
														 
		for (XEvent e : t) 
			{
				
				//Date eventTime = XTimeExtension.instance().extractTimestamp(e);
				//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,e));
				//long duration = eventTime.getTime()-matchEventTime.getTime();
			
				String eventName = XConceptExtension.instance().extractName(e);
				String lifecycle = XLifecycleExtension.instance().extractTransition(e);
				
				if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
				{
					XAttributeDiscrete a_duration = (XAttributeDiscrete)e.getAttributes().get("time:duration");
					long duration = a_duration.getValue();
					durlogs.add((double)duration);
				}
			}
							
		}
		//----
		Double threshold = stat.getMedianThreshold(durlogs, distribution_param, stdev_threshold);
		//Double threshold = stat.getAverageThreshold(durlogs, stdev_threshold);
				
		XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
			
					
					for (XTrace t : log) {
						XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
						copylog.add(trace);
						
											 
					for (XEvent e : t) 
						{
						XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
						
							//Date eventTime = XTimeExtension.instance().extractTimestamp(event);
							//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,event));
							//long duration = (eventTime.getTime()-matchEventTime.getTime())/60000;
						String eventName = XConceptExtension.instance().extractName(event);
						
						
						
						String lifecycle = XLifecycleExtension.instance().extractTransition(event);
						
						if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name)){
							
							if (eventName.equals("IP Access"))
							{
								context.log("IP Access: "+threshold.toString());
							}
							
							XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
							long duration = a_duration.getValue();
														
							if (duration > threshold)
							{
								event.getAttributes().put("feature:act_duration",new XAttributeBooleanImpl("feature:act_duration",true));
								event.getAttributes().put("feature:ab_duration",new XAttributeContinuousImpl("feature:ab_duration",duration-java.lang.Math.pow(10,threshold)));
							
							}
						}
							trace.add(event);
						}
										
					}
		return copylog;
		
	};
	
	//-----------------------------------------Average---------------------------------------------------------
	
public static XLog getRiskyAverage (UIPluginContext context, XLog log, String act_name, Double distribution_param, Double stdev_threshold){
			
			//----
			Vector<Double> durlogs = new Vector<Double>();	
					
			// getting logarithms of durations
			for (XTrace t : log) {
															 
			for (XEvent e : t) 
				{
					
					//Date eventTime = XTimeExtension.instance().extractTimestamp(e);
					//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,e));
					//long duration = eventTime.getTime()-matchEventTime.getTime();
				
					String eventName = XConceptExtension.instance().extractName(e);
					String lifecycle = XLifecycleExtension.instance().extractTransition(e);
					
					if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
					{
						XAttributeDiscrete a_duration = (XAttributeDiscrete)e.getAttributes().get("time:duration");
						long duration = a_duration.getValue();
						durlogs.add((double)duration);
					}
				}
								
			}
			//----
			//Double threshold = stat.getMedianThreshold(durlogs, distribution_param, stdev_threshold);
			Double threshold = stat.getAverageThreshold(durlogs, stdev_threshold);
					
			XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
				
						
						for (XTrace t : log) {
							XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
							copylog.add(trace);
							
												 
						for (XEvent e : t) 
							{
							XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
							
								//Date eventTime = XTimeExtension.instance().extractTimestamp(event);
								//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,event));
								//long duration = (eventTime.getTime()-matchEventTime.getTime())/60000;
							String eventName = XConceptExtension.instance().extractName(event);
							
							
							
							String lifecycle = XLifecycleExtension.instance().extractTransition(event);
							
							if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name)){
								
								if (eventName.equals("IP Access"))
								{
									context.log("IP Access: "+threshold.toString());
								}
								
								XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
								long duration = a_duration.getValue();
																
								if (duration > threshold)
								{
									event.getAttributes().put("feature:act_duration",new XAttributeBooleanImpl("feature:act_duration",true));
									event.getAttributes().put("feature:ab_duration",new XAttributeContinuousImpl("feature:ab_duration",duration-java.lang.Math.pow(10,threshold)));
								
								}
							}
								trace.add(event);
							}
											
						}
			return copylog;
			
		};
		
//-----------------------------------------Logarithm Average---------------------------------------------------------
		
public static XLog getRiskyLogAverage (UIPluginContext context, XLog log, String act_name, Double distribution_param, Double stdev_threshold){
			
			//----
			Vector<Double> durlogs = new Vector<Double>();	
					
			// getting logarithms of durations
			for (XTrace t : log) {
															 
			for (XEvent e : t) 
				{
					
					//Date eventTime = XTimeExtension.instance().extractTimestamp(e);
					//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,e));
					//long duration = eventTime.getTime()-matchEventTime.getTime();
				
					String eventName = XConceptExtension.instance().extractName(e);
					String lifecycle = XLifecycleExtension.instance().extractTransition(e);
					
					if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
					{
						XAttributeDiscrete a_duration = (XAttributeDiscrete)e.getAttributes().get("time:duration");
						long duration = a_duration.getValue();
						durlogs.add(Math.log10(duration));
					}
				}
								
			}
			//----
			//Double threshold = stat.getMedianThreshold(durlogs, distribution_param, stdev_threshold);
			Double threshold = stat.getAverageThreshold(durlogs, stdev_threshold);
					
			XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
				
						
						for (XTrace t : log) {
							XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
							copylog.add(trace);
							
												 
						for (XEvent e : t) 
							{
							XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
							
								//Date eventTime = XTimeExtension.instance().extractTimestamp(event);
								//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,event));
								//long duration = (eventTime.getTime()-matchEventTime.getTime())/60000;
							String eventName = XConceptExtension.instance().extractName(event);
							
							
							
							String lifecycle = XLifecycleExtension.instance().extractTransition(event);
							
							if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name)){
								
								if (eventName.equals("IP Access"))
								{
									context.log("IP Access: "+threshold.toString());
								}
								
								XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
								long duration = a_duration.getValue();
								Double logduration = Math.log10(duration);
								
								if (logduration > threshold)
								{
									event.getAttributes().put("feature:act_duration",new XAttributeBooleanImpl("feature:act_duration",true));
									event.getAttributes().put("feature:ab_duration",new XAttributeContinuousImpl("feature:ab_duration",duration-java.lang.Math.pow(10,threshold)));
								
								}
							}
								trace.add(event);
							}
											
						}
			return copylog;
			
		};
		
//-----------------------------------------Logarithm Median with code split---------------------------------------------------------
		
			public static XLog getRiskyLogMedianSplit (UIPluginContext context, XLog log, String act_name, Double distribution_param, Double stdev_threshold){
				
				int i = 0;
				int splitIndex = 0;
				int size = log.size();
				
				if( (size % 2) == 0){splitIndex = size/2;}else{splitIndex = (size+1)/2;}	
											
				//------------------------------------------------------------------------
				Vector<Double> durlogs = new Vector<Double>();	
						
				// getting logarithms of durations
				for (XTrace t : log) {
					
				if (i < splitIndex)
				{	
					
				for (XEvent e : t) 
					{
						
						//Date eventTime = XTimeExtension.instance().extractTimestamp(e);
						//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,e));
						//long duration = eventTime.getTime()-matchEventTime.getTime();
					
						String eventName = XConceptExtension.instance().extractName(e);
						String lifecycle = XLifecycleExtension.instance().extractTransition(e);
						
						if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
						{
							XAttributeDiscrete a_duration = (XAttributeDiscrete)e.getAttributes().get("time:duration");
							long duration = a_duration.getValue();
							
							//if (duration > 0){durlogs.add(Math.log10(duration));}
							
							
							durlogs.add(Math.log10(duration));
						}
					}
				}i++;
									
				}
				//----
				Double threshold = stat.getMedianThreshold(durlogs, distribution_param, stdev_threshold);
				//Double threshold = stat.getAverageThreshold(durlogs, stdev_threshold);
						
				XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
					
							i=0;
							for (XTrace t : log) {
								XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
								copylog.add(trace);
								
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
								
									//Date eventTime = XTimeExtension.instance().extractTimestamp(event);
									//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,event));
									//long duration = (eventTime.getTime()-matchEventTime.getTime())/60000;
							
							if (i>=splitIndex)
							{	trace.getAttributes().put("set:test",new XAttributeDiscreteImpl("set:test",1));
								String eventName = XConceptExtension.instance().extractName(event);
								String lifecycle = XLifecycleExtension.instance().extractTransition(event);
								
								if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name)){
									
																
									XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
									long duration = a_duration.getValue();
									Double logduration = Math.log10(duration);
									
									if (logduration > threshold)
									{	
										//---------------updating discovery time-----------------------------------------
										
										String current_discovered = t.getAttributes().get("time:PRI1").toString();
										Long current = Long.parseLong(current_discovered);
										Date end = XTimeExtension.instance().extractTimestamp(event);
										long discovered = end.getTime()-begin.getTime();
										
										if(current > discovered || current_discovered.equals("0"))
									
									{
										
										trace.getAttributes().put("time:PRI1",new XAttributeDiscreteImpl("time:PRI1",discovered));
									}
										//--------------------------------------------------------------------------------
										event.getAttributes().put("feature:act_duration",new XAttributeBooleanImpl("feature:act_duration",true));
										event.getAttributes().put("feature:ab_duration",new XAttributeContinuousImpl("feature:ab_duration",duration-java.lang.Math.pow(10,threshold)));
									
									}
								}
							}
									trace.add(event);
								}
												
							i++;}
				return copylog;
				
			};
			
			//-----------------------------------------Logarithm Median---------------------------------------------------------
			
			public static XLog getRiskyLogMedian (UIPluginContext context, XLog log, String act_name, Double distribution_param, Double stdev_threshold){
				
				//----
				Vector<Double> durlogs = new Vector<Double>();	
						
				// getting logarithms of durations
				for (XTrace t : log) {
																 
				for (XEvent e : t) 
					{
						
						//Date eventTime = XTimeExtension.instance().extractTimestamp(e);
						//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,e));
						//long duration = eventTime.getTime()-matchEventTime.getTime();
					
						String eventName = XConceptExtension.instance().extractName(e);
						String lifecycle = XLifecycleExtension.instance().extractTransition(e);
						
						if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
						{
							XAttributeDiscrete a_duration = (XAttributeDiscrete)e.getAttributes().get("time:duration");
							long duration = a_duration.getValue();
							durlogs.add(Math.log10(duration));
						}
					}
									
				}
				//----
				Double threshold = stat.getMedianThreshold(durlogs, distribution_param, stdev_threshold);
				//Double threshold = stat.getAverageThreshold(durlogs, stdev_threshold);
						
				XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
					
							
							for (XTrace t : log) {
								XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
								copylog.add(trace);
								
													 
							for (XEvent e : t) 
								{
								XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
								
									//Date eventTime = XTimeExtension.instance().extractTimestamp(event);
									//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,event));
									//long duration = (eventTime.getTime()-matchEventTime.getTime())/60000;
								String eventName = XConceptExtension.instance().extractName(event);
								
								
								
								String lifecycle = XLifecycleExtension.instance().extractTransition(event);
								
								if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name)){
									
									if (eventName.equals("IP Access"))
									{
										context.log("IP Access: "+threshold.toString());
									}
									
									XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
									long duration = a_duration.getValue();
									Double logduration = Math.log10(duration);
									
									if (logduration > threshold)
									{
										event.getAttributes().put("feature:act_duration",new XAttributeBooleanImpl("feature:act_duration",true));
										event.getAttributes().put("feature:ab_duration",new XAttributeContinuousImpl("feature:ab_duration",duration-java.lang.Math.pow(10,threshold)));
									
									}
								}
									trace.add(event);
								}
												
							}
				return copylog;
				
			};
*/	
