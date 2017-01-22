package org.processmining.processriskindicators.pri;

import java.util.Date;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeBooleanImpl;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.processmining.processriskindicators.analysis.Basic;
import org.processmining.processriskindicators.analysis.Stat;


public class AbnormalWaitDuration {

	static Basic basic = new Basic();
	static Stat stat = new Stat();
	
	public static XLog getRiskyLogMedianTwoLogs (XLog trainlog, XLog testlog, String act_name, Double distribution_param, Double stdev_threshold){
					
					Vector<Double> waitDur = new Vector<Double>();	
							
					// getting wait durations
					for (XTrace t : trainlog) {
																	 
					for (XEvent e : t) 
						{
						String lifecycle = XLifecycleExtension.instance().extractTransition(e);
						String eventName = XConceptExtension.instance().extractName(e);
						
						if (lifecycle.equalsIgnoreCase("start") && eventName.equals(act_name) && t.indexOf(e)!=0)
						{
							Date eventTime = XTimeExtension.instance().extractTimestamp(e);
							Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
							long duration = eventTime.getTime()-prevEventTime.getTime();
							waitDur.add(Math.log10(duration));				
							}
						}
										
					}
					
					Double threshold = stat.getMedianThreshold(waitDur, distribution_param, stdev_threshold);
								
					XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(testlog.getAttributes());
						
								
								for (XTrace t : testlog) {
									XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
																	 
								for (XEvent e : t) 
									{
									XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
									String eventName = XConceptExtension.instance().extractName(event);
									String lifecycle = XLifecycleExtension.instance().extractTransition(event);
									
									if (lifecycle.equalsIgnoreCase("start")  && eventName.equals(act_name) && t.indexOf(e)!=0)
									{
										Date eventTime = XTimeExtension.instance().extractTimestamp(event);
										Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
										long duration = eventTime.getTime()-prevEventTime.getTime();
										Double logduration = Math.log10(duration);
										
										if (logduration > threshold)
										{
											event.getAttributes().put("feature:wait_duration",new XAttributeBooleanImpl("feature:wait_duration",true));
											event.getAttributes().put("feature:ab_wait_duration",new XAttributeContinuousImpl("feature:ab_wait_duration",duration-java.lang.Math.pow(10,threshold)));
										
										}
									}
										trace.add(event);
									}
								copylog.add(trace);				
								}
					return copylog;
					
				};	
}

//--------------------------------PREV. VERSIONS-------------------------------------------------------------------------------
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
	
//----------------------------------------------------Risky annotate---------------------------------------------------------
		public static XLog Risky (XLog trainlog, XLog testlog, String act_name){
					
					//----
					Vector<Double> waitDur = new Vector<Double>();	
							
					for (XTrace t : trainlog) {
																	 
					for (XEvent e : t) 
						{
						String lifecycle = XLifecycleExtension.instance().extractTransition(e);
						String eventName = XConceptExtension.instance().extractName(e);
						
						if (lifecycle.equalsIgnoreCase("start") && eventName.equals(act_name) && t.indexOf(e)!=0)
						{
							Date eventTime = XTimeExtension.instance().extractTimestamp(e);
							Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
							long duration = eventTime.getTime()-prevEventTime.getTime();
							waitDur.add((double)duration);				
							}
						}
										
					}
					//----
				
					Double average = stat.Average(waitDur);
					Double stDev = stat.getStDev(waitDur);
							
					XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(testlog.getAttributes());
						
								
								for (XTrace t : testlog) {
									XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
																	 
								for (XEvent e : t) 
									{
									XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
									String eventName = XConceptExtension.instance().extractName(event);
									String lifecycle = XLifecycleExtension.instance().extractTransition(event);
									
									if (lifecycle.equalsIgnoreCase("start")  && eventName.equals(act_name) && t.indexOf(e)!=0)
									{
										Date eventTime = XTimeExtension.instance().extractTimestamp(event);
										Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
										long duration = eventTime.getTime()-prevEventTime.getTime();
																				
										
											//event.getAttributes().put("feature:wait_duration",new XAttributeBooleanImpl("feature:wait_duration",true));
											
											event.getAttributes().put("PRI2:average",new XAttributeContinuousImpl("PRI2:average",average));
											event.getAttributes().put("PRI2:stDev",new XAttributeContinuousImpl("PRI2:stDev",stDev));
											event.getAttributes().put("PRI2:deviation",new XAttributeContinuousImpl("PRI2:deviation",(duration-average)/stDev));
										
										
									}
										trace.add(event);
									}
								copylog.add(trace);				
								}
					return copylog;
					
				};	
	
	

	
//----------------------------------------------------Logarithm Average-with two logs----------------------------------------------------------
	public static XLog getRiskyLogAverageTwoLogs (XLog trainlog, XLog testlog, String act_name, Double distribution_param, Double stdev_threshold){
				
				//----
				Vector<Double> waitDur = new Vector<Double>();	
						
				// getting wait durations
				for (XTrace t : trainlog) {
																 
				for (XEvent e : t) 
					{
					String lifecycle = XLifecycleExtension.instance().extractTransition(e);
					String eventName = XConceptExtension.instance().extractName(e);
					
					if (lifecycle.equalsIgnoreCase("start") && eventName.equals(act_name) && t.indexOf(e)!=0)
					{
						Date eventTime = XTimeExtension.instance().extractTimestamp(e);
						Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
						long duration = eventTime.getTime()-prevEventTime.getTime();
						waitDur.add(Math.log10(duration));				
						}
					}
									
				}
				//----
				Double threshold = stat.getAverageThreshold(waitDur, stdev_threshold);
				//Double threshold = -10.00;
						
				XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(testlog.getAttributes());
					
							
							for (XTrace t : testlog) {
								XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
																 
							for (XEvent e : t) 
								{
								XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
								String eventName = XConceptExtension.instance().extractName(event);
								String lifecycle = XLifecycleExtension.instance().extractTransition(event);
								
								if (lifecycle.equalsIgnoreCase("start")  && eventName.equals(act_name) && t.indexOf(e)!=0)
								{
									Date eventTime = XTimeExtension.instance().extractTimestamp(event);
									Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
									long duration = eventTime.getTime()-prevEventTime.getTime();
									Double logduration = Math.log10(duration);
									
									if (logduration > threshold)
									{
										event.getAttributes().put("feature:wait_duration",new XAttributeBooleanImpl("feature:wait_duration",true));
										event.getAttributes().put("feature:ab_wait_duration",new XAttributeContinuousImpl("feature:ab_wait_duration",duration-java.lang.Math.pow(10,threshold)));
									
									}
								}
									trace.add(event);
								}
							copylog.add(trace);				
							}
				return copylog;
				
			};
			
//----------------------------------------------------Median-----------------------------------------------------------
	public static XLog getRiskyMedian (XLog log, String act_name, Double distribution_param, Double stdev_threshold){
		
		//----
		Vector<Double> waitDur = new Vector<Double>();	
				
		// getting wait durations
		for (XTrace t : log) {
														 
		for (XEvent e : t) 
			{
			String lifecycle = XLifecycleExtension.instance().extractTransition(e);
			String eventName = XConceptExtension.instance().extractName(e);
			
			if (lifecycle.equalsIgnoreCase("start") && eventName.equals(act_name) && t.indexOf(e)!=0)
			{
				Date eventTime = XTimeExtension.instance().extractTimestamp(e);
				Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
				long duration = eventTime.getTime()-prevEventTime.getTime();
				double dur = duration;
								
				waitDur.add(dur);
				}
			}
							
		}
		//----
		Double threshold = stat.getMedianThreshold(waitDur, distribution_param, stdev_threshold);
		//Double threshold = -10.00;
				
		XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
			
					
					for (XTrace t : log) {
						XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
														 
					for (XEvent e : t) 
						{
						XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
						String eventName = XConceptExtension.instance().extractName(event);
						String lifecycle = XLifecycleExtension.instance().extractTransition(event);
						
						if (lifecycle.equalsIgnoreCase("start")  && eventName.equals(act_name) && t.indexOf(e)!=0)
						{
							Date eventTime = XTimeExtension.instance().extractTimestamp(event);
							Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
							long duration = eventTime.getTime()-prevEventTime.getTime();
							double waitdur = duration;
							
							
							if (waitdur > threshold)
							{
								event.getAttributes().put("feature:wait_duration",new XAttributeBooleanImpl("feature:wait_duration",true));
								event.getAttributes().put("feature:ab_wait_duration",new XAttributeContinuousImpl("feature:ab_wait_duration",waitdur-threshold));
							
							}
						}
							trace.add(event);
						}
					copylog.add(trace);				
					}
		return copylog;
		
	};

//----------------------------------------------------Logarithm Median-----------------------------------------------------------
		public static XLog getRiskyLogMedian (XLog log, String act_name, Double distribution_param, Double stdev_threshold){
			
			//----
			Vector<Double> waitDur = new Vector<Double>();	
					
			// getting wait durations
			for (XTrace t : log) {
															 
			for (XEvent e : t) 
				{
				String lifecycle = XLifecycleExtension.instance().extractTransition(e);
				String eventName = XConceptExtension.instance().extractName(e);
				
				if (lifecycle.equalsIgnoreCase("start") && eventName.equals(act_name) && t.indexOf(e)!=0)
				{
					Date eventTime = XTimeExtension.instance().extractTimestamp(e);
					Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
					long duration = eventTime.getTime()-prevEventTime.getTime();
					waitDur.add(Math.log10(duration));				
					}
				}
								
			}
			//----
			Double threshold = stat.getMedianThreshold(waitDur, distribution_param, stdev_threshold);
			//Double threshold = -10.00;
					
			XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
				
						
						for (XTrace t : log) {
							XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
															 
						for (XEvent e : t) 
							{
							XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
							String eventName = XConceptExtension.instance().extractName(event);
							String lifecycle = XLifecycleExtension.instance().extractTransition(event);
							
							if (lifecycle.equalsIgnoreCase("start")  && eventName.equals(act_name) && t.indexOf(e)!=0)
							{
								Date eventTime = XTimeExtension.instance().extractTimestamp(event);
								Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
								long duration = eventTime.getTime()-prevEventTime.getTime();
								Double logduration = Math.log10(duration);
								
								if (logduration > threshold)
								{
									event.getAttributes().put("feature:wait_duration",new XAttributeBooleanImpl("feature:wait_duration",true));
									event.getAttributes().put("feature:ab_wait_duration",new XAttributeContinuousImpl("feature:ab_wait_duration",duration-java.lang.Math.pow(10,threshold)));
								
								}
							}
								trace.add(event);
							}
						copylog.add(trace);				
						}
			return copylog;
			
		};
	
		
		//----------------------------------------------------Average-----------------------------------------------------------
		public static XLog getRiskyAverage (XLog log, String act_name, Double distribution_param, Double stdev_threshold){
			
			//----
			Vector<Double> waitDur = new Vector<Double>();	
					
			// getting wait durations
			for (XTrace t : log) {
															 
			for (XEvent e : t) 
				{
				String lifecycle = XLifecycleExtension.instance().extractTransition(e);
				String eventName = XConceptExtension.instance().extractName(e);
				
				if (lifecycle.equalsIgnoreCase("start") && eventName.equals(act_name) && t.indexOf(e)!=0)
				{
					Date eventTime = XTimeExtension.instance().extractTimestamp(e);
					Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
					long duration = eventTime.getTime()-prevEventTime.getTime();
					double dur = duration;
									
					waitDur.add(dur);
					}
				}
								
			}
			//----
			Double threshold = stat.getAverageThreshold(waitDur, stdev_threshold);
			//Double threshold = -10.00;
					
			XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
				
						
						for (XTrace t : log) {
							XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
															 
						for (XEvent e : t) 
							{
							XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
							String eventName = XConceptExtension.instance().extractName(event);
							String lifecycle = XLifecycleExtension.instance().extractTransition(event);
							
							if (lifecycle.equalsIgnoreCase("start")  && eventName.equals(act_name) && t.indexOf(e)!=0)
							{
								Date eventTime = XTimeExtension.instance().extractTimestamp(event);
								Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
								long duration = eventTime.getTime()-prevEventTime.getTime();
								double waitdur = duration;
								
								
								if (waitdur > threshold)
								{
									event.getAttributes().put("feature:wait_duration",new XAttributeBooleanImpl("feature:wait_duration",true));
									event.getAttributes().put("feature:ab_wait_duration",new XAttributeContinuousImpl("feature:ab_wait_duration",waitdur-threshold));
								
								}
							}
								trace.add(event);
							}
						copylog.add(trace);				
						}
			return copylog;
			
		};

	//----------------------------------------------------Logarithm Average-----------------------------------------------------------
			public static XLog getRiskyLogAverage (XLog log, String act_name, Double distribution_param, Double stdev_threshold){
				
				//----
				Vector<Double> waitDur = new Vector<Double>();	
						
				// getting wait durations
				for (XTrace t : log) {
																 
				for (XEvent e : t) 
					{
					String lifecycle = XLifecycleExtension.instance().extractTransition(e);
					String eventName = XConceptExtension.instance().extractName(e);
					
					if (lifecycle.equalsIgnoreCase("start") && eventName.equals(act_name) && t.indexOf(e)!=0)
					{
						Date eventTime = XTimeExtension.instance().extractTimestamp(e);
						Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
						long duration = eventTime.getTime()-prevEventTime.getTime();
						waitDur.add(Math.log10(duration));				
						}
					}
									
				}
				//----
				Double threshold = stat.getAverageThreshold(waitDur, stdev_threshold);
				//Double threshold = -10.00;
						
				XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
					
							
							for (XTrace t : log) {
								XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
																 
							for (XEvent e : t) 
								{
								XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
								String eventName = XConceptExtension.instance().extractName(event);
								String lifecycle = XLifecycleExtension.instance().extractTransition(event);
								
								if (lifecycle.equalsIgnoreCase("start")  && eventName.equals(act_name) && t.indexOf(e)!=0)
								{
									Date eventTime = XTimeExtension.instance().extractTimestamp(event);
									Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
									long duration = eventTime.getTime()-prevEventTime.getTime();
									Double logduration = Math.log10(duration);
									
									if (logduration > threshold)
									{
										event.getAttributes().put("feature:wait_duration",new XAttributeBooleanImpl("feature:wait_duration",true));
										event.getAttributes().put("feature:ab_wait_duration",new XAttributeContinuousImpl("feature:ab_wait_duration",duration-java.lang.Math.pow(10,threshold)));
									
									}
								}
									trace.add(event);
								}
							copylog.add(trace);				
							}
				return copylog;
				
			};
		
			//----------------------------------------------------Logarithm Median-with split----------------------------------------------------------
			public static XLog getRiskyLogMedianSplit (XLog log, String act_name, Double distribution_param, Double stdev_threshold){
				
				int i = 0;
				int splitIndex = 0;
				int size = log.size();
				
				if( (size % 2) == 0){splitIndex = size/2;}else{splitIndex = (size+1)/2;}	
						
				//----
				Vector<Double> waitDur = new Vector<Double>();	
						
				// getting wait durations
				for (XTrace t : log) {
															 
				for (XEvent e : t) 
				{if(i<splitIndex){	
					String lifecycle = XLifecycleExtension.instance().extractTransition(e);
					String eventName = XConceptExtension.instance().extractName(e);
					
					if (lifecycle.equalsIgnoreCase("start") && eventName.equals(act_name) && t.indexOf(e)!=0)
					{
						Date eventTime = XTimeExtension.instance().extractTimestamp(e);
						Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
						long duration = eventTime.getTime()-prevEventTime.getTime();
						waitDur.add(Math.log10(duration));				
						}
					}
					}	i++;			
				}
				//----
				Double threshold = stat.getMedianThreshold(waitDur, distribution_param, stdev_threshold);
				//Double threshold = -10.00;
						
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
								if(i>=splitIndex){
								String eventName = XConceptExtension.instance().extractName(event);
								String lifecycle = XLifecycleExtension.instance().extractTransition(event);
								
								if (lifecycle.equalsIgnoreCase("start")  && eventName.equals(act_name) && t.indexOf(e)!=0)
								{
									Date eventTime = XTimeExtension.instance().extractTimestamp(event);
									Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
									long duration = eventTime.getTime()-prevEventTime.getTime();
									Double logduration = Math.log10(duration);
									
									if (logduration > threshold)
									{
										//---------------updating discovery time-----------------------------------------
										String current_discovered = t.getAttributes().get("time:PRI2").toString();
										Date end = XTimeExtension.instance().extractTimestamp(event);
										long discovered = end.getTime()-begin.getTime();
										Long current = Long.parseLong(current_discovered);
										if(current > discovered || current_discovered.equals("0"))
									
									{
										
										trace.getAttributes().put("time:PRI2",new XAttributeDiscreteImpl("time:PRI2",discovered));
									}
										//--------------------------------------------------------------------------------
							
										
										event.getAttributes().put("feature:wait_duration",new XAttributeBooleanImpl("feature:wait_duration",true));
										event.getAttributes().put("feature:ab_wait_duration",new XAttributeContinuousImpl("feature:ab_wait_duration",duration-java.lang.Math.pow(10,threshold)));
									
									}
								}}
									trace.add(event);
								}i++;
							copylog.add(trace);				
							}
				return copylog;
				
			};
	
	//----------------------------------------------------Median with split-----------------------------------------------------------
		public static XLog getRiskyMedianSplit (XLog log, String act_name, Double distribution_param, Double stdev_threshold){
			
			int i = 0;
			int splitIndex = 0;
			int size = log.size();
			
			if( (size % 2) == 0){splitIndex = size/2;}else{splitIndex = (size+1)/2;}	
					
			//----
			Vector<Double> waitDur = new Vector<Double>();	
					
			// getting wait durations
			for (XTrace t : log) {
				
				if (i < splitIndex)
				{														 
			for (XEvent e : t) 
				{
				String lifecycle = XLifecycleExtension.instance().extractTransition(e);
				String eventName = XConceptExtension.instance().extractName(e);
				
				if (lifecycle.equalsIgnoreCase("start") && eventName.equals(act_name) && t.indexOf(e)!=0)
				{
					Date eventTime = XTimeExtension.instance().extractTimestamp(e);
					Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
					long duration = eventTime.getTime()-prevEventTime.getTime();
					double dur = duration;
									
					waitDur.add(dur);
					}
				}
			}i++;
								
			}
			//----
			Double threshold = stat.getMedianThreshold(waitDur, distribution_param, stdev_threshold);
			//Double threshold = -10.00;
					
			XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
				
						i=0;
						for (XTrace t : log) {
							XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
							if (i<splitIndex)
							{
								trace.getAttributes().put("outcome:case_duration",new XAttributeBooleanImpl("outcome:case_duration",false));
								trace.getAttributes().put("outcome:SLA_violation",new XAttributeContinuousImpl("outcome:SLA_violation",0.00));
					
							}								 
						for (XEvent e : t) 
							{
							XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
							String eventName = XConceptExtension.instance().extractName(event);
							String lifecycle = XLifecycleExtension.instance().extractTransition(event);
						if (i>=splitIndex)	{
							
						
							if (lifecycle.equalsIgnoreCase("start")  && eventName.equals(act_name) && t.indexOf(e)!=0)
							{
								Date eventTime = XTimeExtension.instance().extractTimestamp(event);
								Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.Prev(t,e));
								long duration = eventTime.getTime()-prevEventTime.getTime();
								double waitdur = duration;
								
								
								if (waitdur > threshold)
								{
									event.getAttributes().put("feature:wait_duration",new XAttributeBooleanImpl("feature:wait_duration",true));
									event.getAttributes().put("feature:ab_wait_duration",new XAttributeContinuousImpl("feature:ab_wait_duration",waitdur-threshold));
								
								}
							}
							
						}
								trace.add(event);
							}i++;
						copylog.add(trace);				
						}
			return copylog;
			
		};

*/

