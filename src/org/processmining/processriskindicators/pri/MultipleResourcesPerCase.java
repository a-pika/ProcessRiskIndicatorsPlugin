package org.processmining.processriskindicators.pri;

import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListSet;

import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeBooleanImpl;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.processmining.processriskindicators.analysis.Stat;


public class MultipleResourcesPerCase {

	static Stat stat = new Stat();
	
	public static XLog getRiskyMedianTwoLogs (XLog trainLog, XLog testLog, String act_name, Double distribution_param, Double stdev_threshold){
				
		//getting number of resources involved in a case
					Vector<Double> numberOfResInCase = new Vector<Double>();
					
				for (XTrace t : trainLog) {
					ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();		 
				for (XEvent e : t) 
					{
					
					String resource = XOrganizationalExtension.instance().extractResource(e);
					if(resource != null)
						{resources.add(resource);}
					
					}
				double num = resources.size();
				numberOfResInCase.add(num);
				}
				
				Double threshold = stat.getMedianThreshold(numberOfResInCase, distribution_param, stdev_threshold);
				
				XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(testLog.getAttributes());
					
							
							for (XTrace t : testLog) {
								XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
								ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();							 
							for (XEvent e : t) 
								{
								XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
								String resource = XOrganizationalExtension.instance().extractResource(event);
								if(resource != null)
								{resources.add(resource);}
								trace.add(event);
								}
							double num = resources.size();
							if (num > threshold)
							{
								trace.getAttributes().put("feature:multiple_resources",new XAttributeBooleanImpl("feature:multiple_resources",true));
								trace.getAttributes().put("feature:ab_mult_res",new XAttributeContinuousImpl("feature:ab_mult_res",num - threshold));
							
							
							}
							
							copylog.add(trace);				
							}
				return copylog;
				
			};
}

//------------------------------------------------------PREV. VERSIONS-----------------------------------------------
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*

	//------------------------------------------annotate all risky------------------------------------------------------
	
public static XLog Risky (XLog trainLog, XLog testLog, String act_name){
					
			//getting number of resources involved in a case
						Vector<Double> numberOfResInCase = new Vector<Double>();
						Vector<Double> numberOfActInCase = new Vector<Double>();
						
					for (XTrace t : trainLog) {
						ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();	
						ConcurrentSkipListSet<String> activities = new ConcurrentSkipListSet<String>();
					for (XEvent e : t) 
						{
						
						String resource = XOrganizationalExtension.instance().extractResource(e);
						String activity = XConceptExtension.instance().extractName(e);
						if(resource != null)
							{resources.add(resource);}
						if(activity != null)
						{activities.add(activity);}
						
						}
					double num = resources.size();
					numberOfResInCase.add(num);
					double actNum = activities.size();
					numberOfActInCase.add(actNum);
					}
					
										
					Double res_average = stat.Average(numberOfResInCase);
					Double act_average = stat.Average(numberOfActInCase);
					Double res_stDev = stat.getStDev(numberOfResInCase);
					Double act_stDev = stat.getStDev(numberOfActInCase);
					
					
										
					XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(testLog.getAttributes());
						
								
								for (XTrace t : testLog) {
									XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
									ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();	
									ConcurrentSkipListSet<String> activities = new ConcurrentSkipListSet<String>();
								for (XEvent e : t) 
									{
									XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
									String resource = XOrganizationalExtension.instance().extractResource(event);
									String activity = XConceptExtension.instance().extractName(e);
									if(resource != null)
									{resources.add(resource);}
									if(activity != null)
									{activities.add(activity);}
									trace.add(event);
									}
								double num = resources.size();
								double actNum = activities.size();
								
								
									//trace.getAttributes().put("feature:multiple_resources",new XAttributeBooleanImpl("feature:multiple_resources",true));
								trace.getAttributes().put("PRI5:average",new XAttributeContinuousImpl("PRI5:average",res_average));
								trace.getAttributes().put("PRI5:stDev",new XAttributeContinuousImpl("PRI5:stDev",res_stDev));
								trace.getAttributes().put("PRI5:deviation",new XAttributeContinuousImpl("PRI5:deviation",(num-res_average)/res_stDev));
							
								trace.getAttributes().put("PRI7:average",new XAttributeContinuousImpl("PRI7:average",act_average));
								trace.getAttributes().put("PRI7:stDev",new XAttributeContinuousImpl("PRI7:stDev",act_stDev));
								trace.getAttributes().put("PRI7:deviation",new XAttributeContinuousImpl("PRI7:deviation",(actNum-act_average)/act_stDev));
							
								copylog.add(trace);				
								}
					return copylog;
					
				};


//-------------------------------------------Median threshold-------------------------------------------------------
	
public static XLog getRiskyMedian (XLog log, String act_name, Double distribution_param, Double stdev_threshold){
			
	//getting number of resources involved in a case
				Vector<Double> numberOfResInCase = new Vector<Double>();
				
			for (XTrace t : log) {
				ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();		 
			for (XEvent e : t) 
				{
				
				String resource = XOrganizationalExtension.instance().extractResource(e);
				if(resource != null)
					{resources.add(resource);}
				
				}
			double num = resources.size();
			numberOfResInCase.add(num);
			}
			
			Double threshold = stat.getMedianThreshold(numberOfResInCase, distribution_param, stdev_threshold);
			
			//Double threshold = stat.getCaseResourcesAverageThreshold(log, stdev_threshold);
								
			XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
				
						
						for (XTrace t : log) {
							XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
							ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();							 
						for (XEvent e : t) 
							{
							XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
							String resource = XOrganizationalExtension.instance().extractResource(event);
							if(resource != null)
							{resources.add(resource);}
							trace.add(event);
							}
						double num = resources.size();
						if (num > threshold)
						{
							trace.getAttributes().put("feature:multiple_resources",new XAttributeBooleanImpl("feature:multiple_resources",true));
							trace.getAttributes().put("feature:ab_mult_res",new XAttributeContinuousImpl("feature:ab_mult_res",num - threshold));
						
						
						}
						
						copylog.add(trace);				
						}
			return copylog;
			
		};
		
//----------------------------------------------median with split-------------
		public static XLog getRiskyMedianSplit (XLog log, Double distribution_param, Double stdev_threshold){
			int i = 0;
			int splitIndex = 0;
			int size = log.size();
			
			if( (size % 2) == 0){splitIndex = size/2;}else{splitIndex = (size+1)/2;}	
										
			//------------------------------------------------------------------------
		
			//getting number of resources involved in a case
						Vector<Double> numberOfResInCase = new Vector<Double>();
						
					for (XTrace t : log) {
						if(i<splitIndex){
						ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();		 
					for (XEvent e : t) 
						{
						
						String resource = XOrganizationalExtension.instance().extractResource(e);
						if(resource != null)
							{resources.add(resource);}
						
						}
					double num = resources.size();
					numberOfResInCase.add(num);}i++;
					}
					
					Double threshold = stat.getMedianThreshold(numberOfResInCase, distribution_param, stdev_threshold);
					
					//Double threshold = stat.getCaseResourcesAverageThreshold(log, stdev_threshold);
										
					XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
						
								i=0;
								for (XTrace t : log) {
									XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
									
									if (i<splitIndex)
									{
										trace.getAttributes().put("outcome:case_duration",new XAttributeBooleanImpl("outcome:case_duration",false));
										trace.getAttributes().put("outcome:SLA_violation",new XAttributeContinuousImpl("outcome:SLA_violation",0.00));
							
									}

										//---------------------case beginning time----------------------------------------
										XEvent first = t.get(0);
										Date begin = XTimeExtension.instance().extractTimestamp(first);
								
										//--------------------------------------------------------------------------------							
											
									ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();							 
								
									for (XEvent e : t) 
									{
									XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
									trace.add(event);
									String current_discovered = t.getAttributes().get("time:PRI5").toString();
									
									if(i>=splitIndex){	
								
									String resource = XOrganizationalExtension.instance().extractResource(event);
									if(resource != null)
									{resources.add(resource);}
											
										double num = resources.size();
										if (num > threshold)
										{
											
											//---------------updating discovery time-----------------------------------------
											if(current_discovered.equals("0"))
										
										{
											Date end = XTimeExtension.instance().extractTimestamp(event);
											long discovered = end.getTime()-begin.getTime();
											trace.getAttributes().put("time:PRI5",new XAttributeDiscreteImpl("time:PRI5",discovered));
											
										}
											//--------------------------------------------------------------------------------
									
											
											trace.getAttributes().put("feature:multiple_resources",new XAttributeBooleanImpl("feature:multiple_resources",true));
											trace.getAttributes().put("feature:ab_mult_res",new XAttributeContinuousImpl("feature:ab_mult_res",num - threshold));
										
										
										}
									
									}
								
								}i++;
								
								copylog.add(trace);				
								}
					return copylog;
					
				};
				
				//-------------------------------------------Average threshold-------------------------------------------------------
				
				public static XLog getRiskyAverage (XLog log, String act_name, Double distribution_param, Double stdev_threshold){
						
						//getting number of resources involved in a case
							Vector<Double> numberOfResInCase = new Vector<Double>();
							
						for (XTrace t : log) {
							ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();		 
						for (XEvent e : t) 
							{
							
							String resource = XOrganizationalExtension.instance().extractResource(e);
							
							if(resource != null)
								{resources.add(resource);}
							
							}
						double num = resources.size();
						numberOfResInCase.add(num);
						}
						
						
						//----
						Double threshold = stat.getAverageThreshold(numberOfResInCase, stdev_threshold);
						
						//Double threshold = stat.getCaseResourcesAverageThreshold(log, stdev_threshold);
						//Double threshold = -10.00;
								
						XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
							
									
									for (XTrace t : log) {
										XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
										ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();							 
									for (XEvent e : t) 
										{
										XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
										String resource = XOrganizationalExtension.instance().extractResource(event);
										if(resource != null)
										{resources.add(resource);}
										trace.add(event);
										}
									double num = resources.size();
									if (num > threshold)
									{
										trace.getAttributes().put("feature:multiple_resources",new XAttributeBooleanImpl("feature:multiple_resources",true));
										trace.getAttributes().put("feature:ab_mult_res",new XAttributeContinuousImpl("feature:ab_mult_res",num - threshold));
									
									
									}
									
									copylog.add(trace);				
									}
						return copylog;
						
					};

*/

