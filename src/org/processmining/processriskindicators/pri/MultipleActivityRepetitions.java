package org.processmining.processriskindicators.pri;

import java.util.Date;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeBooleanImpl;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.processriskindicators.analysis.Basic;
import org.processmining.processriskindicators.analysis.Stat;


public class MultipleActivityRepetitions {

	static Basic basic = new Basic();
	static Stat stat = new Stat();
	
	public static XLog getRiskyMedianTwoLogs (UIPluginContext context, XLog trainLog, XLog testLog, String act_name, Double distribution_param, Double stdev_threshold){
		
		Vector<Double> numExec = new Vector<Double>();	
				
		// getting number of executions of an activity
		for (XTrace t : trainLog) {
			double num = 0;											 
		for (XEvent e : t) 
			{
			String lifecycle = XLifecycleExtension.instance().extractTransition(e);
			String eventName = XConceptExtension.instance().extractName(e);
			
			if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
				{
				num+=1;								
				}
			}
		
		if (num != 0) {numExec.add(num);}	
			}
		
		Double threshold = stat.getMedianThreshold(numExec, distribution_param, stdev_threshold);
		context.log("mult thres: "+threshold);
				
		XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(testLog.getAttributes());
			
					
					for (XTrace t : testLog) {
						XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
						
						
						//---------------------case beginning time----------------------------------------
						XEvent first = t.get(0);
						Date begin = XTimeExtension.instance().extractTimestamp(first);
						
						long totalAbRepDuration = 0;						 
					for (XEvent e : t) 
						{
						XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
						String eventName = XConceptExtension.instance().extractName(event);
						String lifecycle = XLifecycleExtension.instance().extractTransition(event);
						
						if (lifecycle.equalsIgnoreCase("complete")  && eventName.equals(act_name) && (basic.Index(t, e)+1) > threshold)
						{
						
								
						XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
						long duration = a_duration.getValue();
						totalAbRepDuration += duration;
						if (duration > -1){
							
						//---------------updating discovery time-----------------------------------------
							String current_discovered = t.getAttributes().get("time:PRI3").toString();
							Date end = XTimeExtension.instance().extractTimestamp(event);
							long discovered = end.getTime()-begin.getTime();
							Long current = Long.parseLong(current_discovered);
							
							if(current > discovered || current_discovered.equals("0"))
						
						{
							
							trace.getAttributes().put("time:PRI3",new XAttributeDiscreteImpl("time:PRI3",discovered));
						}
					
								
							event.getAttributes().put("feature:act_repetition",new XAttributeBooleanImpl("feature:act_repetition",true));
							event.getAttributes().put("feature:rep_duration",new XAttributeContinuousImpl("feature:rep_duration",duration));
						}
						}
						
							trace.add(event);
						}
					copylog.add(trace);				
					}
		return copylog;
		
	};
	
}

//---------------------------------------------PREV. VERSIONS------------------------------------------------
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*

//-----------------------------------------------------annotate with risks----------------------------------------------------------------
			public static XLog Risky (UIPluginContext context, XLog trainLog, XLog testLog, String act_name){
				
				//---
				Vector<Double> numExec = new Vector<Double>();	
						
				// getting number of executions of an activity
				for (XTrace t : trainLog) {
					double num = 0;											 
				for (XEvent e : t) 
					{
					String lifecycle = XLifecycleExtension.instance().extractTransition(e);
					String eventName = XConceptExtension.instance().extractName(e);
					
					if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
						{
						num+=1;								
						}
					}
				
				if (num != 0) {numExec.add(num);}	
				//numExec.add(num);
				}
				//----
				Double average = stat.Average(numExec);
				Double stDev = stat.getStDev(numExec);
						
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
										
									event.getAttributes().put("PRI3:average",new XAttributeContinuousImpl("PRI3:average",average));
									event.getAttributes().put("PRI3:stDev",new XAttributeContinuousImpl("PRI3:stDev",stDev));
									event.getAttributes().put("PRI3:deviation",new XAttributeContinuousImpl("PRI3:deviation",((basic.Index(t, e)+1)-average)/stDev));
								
								}
								
									trace.add(event);
								}
							copylog.add(trace);				
							}
				return copylog;
				
			};
	
	

	
	//-----------------------------------------------------Median split 2-----------------------------------------------------------------
			public static XLog getRiskyMedianSplit2 (UIPluginContext context, XLog log, String act_name, Double distribution_param, Double stdev_threshold){
				int i = 0;
				int splitIndex = 0;
				int size = log.size();
				
				if( (size % 2) == 0){splitIndex = size/2;}else{splitIndex = (size+1)/2;}	
											
				//------------------------------------------------------------------------
			
				Vector<Double> numExec = new Vector<Double>();	
						
				// getting number of executions of an activity
				for (XTrace t : log) {
					double num = 0;	
					if((i % 2) == 0){
				for (XEvent e : t) 
					{
					String lifecycle = XLifecycleExtension.instance().extractTransition(e);
					String eventName = XConceptExtension.instance().extractName(e);
					
					if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
						{
						num+=1;								
						}
					}}i++;
				
				numExec.add(num);				
				}
				//----
				Double threshold = stat.getMedianThreshold(numExec, distribution_param, stdev_threshold);
				//Double threshold = stat.getAverageThreshold(numExec, stdev_threshold);
				context.log("mult thres: "+threshold);
				//Double threshold = -10.00;
						
				XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
					
							i=0;
							for (XTrace t : log) {
								XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
								if ((i % 2) == 0)
								{
									trace.getAttributes().put("outcome:case_duration",new XAttributeBooleanImpl("outcome:case_duration",false));
									trace.getAttributes().put("outcome:SLA_violation",new XAttributeContinuousImpl("outcome:SLA_violation",0.00));
						
								}
								
							for (XEvent e : t) 
								{
								XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
								if ((i % 2) != 0){
								
								String eventName = XConceptExtension.instance().extractName(event);
								String lifecycle = XLifecycleExtension.instance().extractTransition(event);
								
								if (lifecycle.equalsIgnoreCase("complete")  && eventName.equals(act_name) && (basic.Index(t, e)+1) > threshold)
								{
										//Date eventTime = XTimeExtension.instance().extractTimestamp(event);
										//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,e));
										//long duration = eventTime.getTime()-matchEventTime.getTime();
										//double dur = duration/60000;
									
									XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
									long duration = a_duration.getValue();
									if (duration!=0){
										event.getAttributes().put("feature:act_repetition",new XAttributeBooleanImpl("feature:act_repetition",true));
										event.getAttributes().put("feature:rep_duration",new XAttributeContinuousImpl("feature:rep_duration",duration));
									}
									}
								}
									trace.add(event);
								}i++;
							copylog.add(trace);				
							}
				return copylog;
				
			};
	//-----------------------------------------------------Median split--PRIv2commented---------------------------------------------------------------
		public static XLog getRiskyMedianSplit (UIPluginContext context, XLog log, String act_name, Double distribution_param, Double stdev_threshold){
			int i = 0;
			int splitIndex = 0;
			int size = log.size();
			
			if( (size % 2) == 0){splitIndex = size/2;}else{splitIndex = (size+1)/2;}	
										
			//------------------------------------------------------------------------
						
			Vector<Double> numExec = new Vector<Double>();	
					
			// getting number of executions of an activity
			for (XTrace t : log) {
				
				//------------logging---------------------------------------
				//XAttributeMap am = t.getAttributes();
				//String caseID = am.get("concept:name").toString();
				//context.log("activity in trace: " + caseID + ": " + act_name);
				//----------------------------------------------------------
				
				double num = 0;	
				if(i<splitIndex){
			for (XEvent e : t) 
				{
				String lifecycle = XLifecycleExtension.instance().extractTransition(e);
				String eventName = XConceptExtension.instance().extractName(e);
				
				if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
					{
					num+=1;								
					}
				
				
				}
			//context.log("num: " + num);
			//if (num != 0){numExec.add(num);}
			numExec.add(num);
				}i++;
				
								
			}
			//----
			Double threshold = stat.getMedianThreshold(numExec, distribution_param, stdev_threshold);
			//Double threshold = stat.getAverageThreshold(numExec, stdev_threshold);
						
			context.log("mult thres for activity " + act_name + ": " + threshold);
			
								
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
						long totalAbRepDuration = 0;
							
						for (XEvent e : t) 
							{
							XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
							if (i>=splitIndex){
							
							String eventName = XConceptExtension.instance().extractName(event);
							String lifecycle = XLifecycleExtension.instance().extractTransition(event);
							
							if (lifecycle.equalsIgnoreCase("complete")  && eventName.equals(act_name) && (basic.Index(t, e)+1) > threshold)
							{
									//Date eventTime = XTimeExtension.instance().extractTimestamp(event);
									//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,e));
									//long duration = eventTime.getTime()-matchEventTime.getTime();
									//double dur = duration/60000;
								
										
								//XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("subprocess:duration");
								XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
								long duration = a_duration.getValue();
								totalAbRepDuration += duration;
								long absthreshold = 36000000;
								//long absthreshold = -1;
								//if (totalAbRepDuration > absthreshold){
								if (duration > 0){
								//---------------updating discovery time-----------------------------------------
									String current_discovered = t.getAttributes().get("time:PRI3").toString();
									Date end = XTimeExtension.instance().extractTimestamp(event);
									long discovered = end.getTime()-begin.getTime();
									Long current = Long.parseLong(current_discovered);
									
									if(current > discovered || current_discovered.equals("0"))
								
								{
									
									trace.getAttributes().put("time:PRI3",new XAttributeDiscreteImpl("time:PRI3",discovered));
								}
							
										
									event.getAttributes().put("feature:act_repetition",new XAttributeBooleanImpl("feature:act_repetition",true));
									event.getAttributes().put("feature:rep_duration",new XAttributeContinuousImpl("feature:rep_duration",duration));
								}
								}
							}
								trace.add(event);
							}i++;
						copylog.add(trace);				
						}
			return copylog;
			
		};
//-----------------------------------------------------Median-----------------------------------------------------------------
	public static XLog getRiskyMedian (UIPluginContext context, XLog log, String act_name, Double distribution_param, Double stdev_threshold){
		
		//---
		Vector<Double> numExec = new Vector<Double>();	
				
		// getting number of executions of an activity
		for (XTrace t : log) {
			double num = 0;											 
		for (XEvent e : t) 
			{
			String lifecycle = XLifecycleExtension.instance().extractTransition(e);
			String eventName = XConceptExtension.instance().extractName(e);
			
			if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
				{
				num+=1;								
				}
			}
		
		numExec.add(num);				
		}
		//----
		Double threshold = stat.getMedianThreshold(numExec, distribution_param, stdev_threshold);
		//Double threshold = stat.getAverageThreshold(numExec, stdev_threshold);
		context.log("mult thres: "+threshold);
		//Double threshold = -10.00;
				
		XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
			
					
					for (XTrace t : log) {
						XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
												 
					for (XEvent e : t) 
						{
						XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
						String eventName = XConceptExtension.instance().extractName(event);
						String lifecycle = XLifecycleExtension.instance().extractTransition(event);
						
						if (lifecycle.equalsIgnoreCase("complete")  && eventName.equals(act_name) && (basic.Index(t, e)+1) > threshold)
						{
								//Date eventTime = XTimeExtension.instance().extractTimestamp(event);
								//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,e));
								//long duration = eventTime.getTime()-matchEventTime.getTime();
								//double dur = duration/60000;
							
							XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
							long duration = a_duration.getValue();
							if (duration!=0){
								event.getAttributes().put("feature:act_repetition",new XAttributeBooleanImpl("feature:act_repetition",true));
								event.getAttributes().put("feature:rep_duration",new XAttributeContinuousImpl("feature:rep_duration",duration));
							}
							}
						
							trace.add(event);
						}
					copylog.add(trace);				
					}
		return copylog;
		
	};

//-----------------------------------------------------Average-----------------------------------------------------------------
		public static XLog getRiskyAverage (UIPluginContext context, XLog log, String act_name, Double distribution_param, Double stdev_threshold){
			
			//---
			Vector<Double> numExec = new Vector<Double>();	
					
			// getting number of executions of an activity
			for (XTrace t : log) {
				double num = 0;											 
			for (XEvent e : t) 
				{
				String lifecycle = XLifecycleExtension.instance().extractTransition(e);
				String eventName = XConceptExtension.instance().extractName(e);
				
				if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
					{
					num+=1;								
					}
				}
			
			numExec.add(num);				
			}
			//----
			//Double threshold = stat.getMedianThreshold(numExec, distribution_param, stdev_threshold);
			Double threshold = stat.getAverageThreshold(numExec, stdev_threshold);
			context.log("mult thres: "+threshold);
			//Double threshold = -10.00;
					
			XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
				
						
						for (XTrace t : log) {
							XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
													 
						for (XEvent e : t) 
							{
							XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
							String eventName = XConceptExtension.instance().extractName(event);
							String lifecycle = XLifecycleExtension.instance().extractTransition(event);
							
							if (lifecycle.equalsIgnoreCase("complete")  && eventName.equals(act_name) && (basic.Index(t, e)+1) > threshold)
							{
									//Date eventTime = XTimeExtension.instance().extractTimestamp(event);
									//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,e));
									//long duration = eventTime.getTime()-matchEventTime.getTime();
									//double dur = duration/60000;
								
								XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
								long duration = a_duration.getValue();
								if (duration!=0){
									event.getAttributes().put("feature:act_repetition",new XAttributeBooleanImpl("feature:act_repetition",true));
									event.getAttributes().put("feature:rep_duration",new XAttributeContinuousImpl("feature:rep_duration",duration));
								}
								}
							
								trace.add(event);
							}
						copylog.add(trace);				
						}
			return copylog;
			
		};
		
		//-----------------------------------------------------Average-----------------------------------------------------------------
				public static XLog getRiskyAverageSplit (UIPluginContext context, XLog log, String act_name, Double distribution_param, Double stdev_threshold){
					int i = 0;
					int splitIndex = 0;
					int size = log.size();
					
					if( (size % 2) == 0){splitIndex = size/2;}else{splitIndex = (size+1)/2;}	
				
					//---
					Vector<Double> numExec = new Vector<Double>();	
							
					// getting number of executions of an activity
					for (XTrace t : log) {
						double num = 0;		
						if (i<splitIndex){
					for (XEvent e : t) 
						{
						String lifecycle = XLifecycleExtension.instance().extractTransition(e);
						String eventName = XConceptExtension.instance().extractName(e);
						
						if (lifecycle.equalsIgnoreCase("complete") && eventName.equals(act_name))
							{
							num+=1;								
							}
						}
					
					numExec.add(num);}i++;			
					}
					//----
					//Double threshold = stat.getMedianThreshold(numExec, distribution_param, stdev_threshold);
					Double threshold = stat.getAverageThreshold(numExec, stdev_threshold);
					context.log("mult thres: "+threshold);
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
									if (i>=splitIndex){
									String eventName = XConceptExtension.instance().extractName(event);
									String lifecycle = XLifecycleExtension.instance().extractTransition(event);
									
									if (lifecycle.equalsIgnoreCase("complete")  && eventName.equals(act_name) && (basic.Index(t, e)+1) > threshold)
									{
											//Date eventTime = XTimeExtension.instance().extractTimestamp(event);
											//Date matchEventTime = XTimeExtension.instance().extractTimestamp(basic.Match(t,e));
											//long duration = eventTime.getTime()-matchEventTime.getTime();
											//double dur = duration/60000;
										
										XAttributeDiscrete a_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
										long duration = a_duration.getValue();
										if (duration!=0){
											event.getAttributes().put("feature:act_repetition",new XAttributeBooleanImpl("feature:act_repetition",true));
											event.getAttributes().put("feature:rep_duration",new XAttributeContinuousImpl("feature:rep_duration",duration));
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

