package org.processmining.processriskindicators.analysis;

import java.util.Collections;
import java.util.Vector;

import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;


public class Stat {

public Double Median(Vector<Double> numbers)
	{
		Collections.sort(numbers);
		double median = 0.0;
		int size = numbers.size();
		
		if( (size % 2) != 0)
		{median = numbers.elementAt((size+1)/2-1);}
		else
		{median = 0.5*numbers.elementAt(size/2-1)+0.5*numbers.elementAt(size/2);}
		return median;
	}

public Double getMedianThreshold (Vector<Double> numbers, Double distribution_param, Double number_stdev){

	Double threshold = 0.00;
if (numbers.size()!=0)	{
Vector<Double> MadVals = new Vector<Double>();
	
	Collections.sort(numbers);

	// calculating MAD	    
				
				Double median = Median(numbers);
				for(int i=0;i<numbers.size();i++)
				{MadVals.add(Math.abs(numbers.elementAt(i)-median));}
				Collections.sort(MadVals);
				Double MAD = Median(MadVals);
									
	// calculating threshold
				
	 threshold = median + number_stdev*distribution_param*MAD;}
	
return threshold;
	
};

public Double getMedianLogSLA (XLog log, Double distribution_param, Double StDev){
	
	Vector<Double> durlogs = new Vector<Double>();				
	Vector<Double> MadVals = new Vector<Double>();
	
	// getting logarithms of case durations
				for (XTrace t : log) {
					
					XAttributeDiscrete trace_duration = (XAttributeDiscrete)t.getAttributes().get("time:duration");
					long t_duration = trace_duration.getValue();
					durlogs.add(Math.log10(t_duration));
									
				}
				
				Collections.sort(durlogs);

	// calculating statistics and threshold	    
				
				for(int z=0;z<durlogs.size();z++)
				{MadVals.add(Math.abs(durlogs.elementAt(z)-Median(durlogs)));}
				Collections.sort(MadVals);
				Double MAD = Median(MadVals);
				Double SLA = Median(durlogs)+StDev*distribution_param*MAD;

	
return java.lang.Math.pow(10,SLA);
	
};

}

//------------------------------------------------PREV. VERSIONS----------------------------------------------------
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//-------------------------------------AVERAGE-------------------------------------------------------
/*	
public Double Average(Vector<Double> numbers)
		{
			double average = 0.0;
			int size = numbers.size();
			double sum = 0.00;
			
			for (int i=0; i<size; i++)
			{
				sum+=numbers.elementAt(i);
			}

			average = sum/size;
			
	return average;
		}
	
*///------------------------------standard deviation----------------------------------------------------
/*	
public Double getStDev (Vector<Double> numbers){
					Double StDev = 0.00;
					if (numbers.size()!=0)	{
				Vector<Double> StDevVals = new Vector<Double>();
					
								Double average = Average(numbers);
								for(int i=0;i<numbers.size();i++)
								{StDevVals.add(Math.pow(numbers.elementAt(i)-average,2));}
								StDev = Math.sqrt(Average(StDevVals));
					}
					
				return StDev;
					
				};
	
*///------------------------------------THRESHOLDS-------------------------------------------------------	

// getting a threshold using median

// getting a threshold using average
/*	
public Double getAverageThreshold (Vector<Double> numbers, Double number_stdev){
		
			Double threshold = 0.00;
		if (numbers.size()!=0)	{
		Vector<Double> StDevVals = new Vector<Double>();
			
			// calculating standard deviation	    
						Double average = Average(numbers);
						for(int i=0;i<numbers.size();i++)
						{StDevVals.add(Math.pow(numbers.elementAt(i)-average,2));}
						Double StDev = Math.sqrt(Average(StDevVals));
						
						
			// calculating threshold
						
			 threshold = average + number_stdev*StDev;}
			
		return threshold;
			
		};


*/	
// getting a left tail median threshold
/*	
		public Double getLeftMedianThreshold (Vector<Double> numbers, Double distribution_param, Double number_stdev){
					
			Double threshold = 0.00;
			if (numbers.size()!=0)	{
			Vector<Double> MadVals = new Vector<Double>();
			
			Collections.sort(numbers);

			// calculating MAD	    
						
						for(int i=0;i<numbers.size();i++)
						{MadVals.add(Math.abs(numbers.elementAt(i)-Median(numbers)));}
						Collections.sort(MadVals);
						Double MAD = Median(MadVals);
						
			// calculating threshold
						
			threshold = Median(numbers)- number_stdev*distribution_param*MAD;}
			
		return threshold;
			
		};
		
*/// getting a left tail average threshold
/*		
			public Double getLeftAverageThreshold (Vector<Double> numbers, Double number_stdev){
			
				Double threshold = 0.00;
			if (numbers.size()!=0)	{
			Vector<Double> StDevVals = new Vector<Double>();
				
				// calculating standard deviation	    
							Double average = Average(numbers);
							for(int i=0;i<numbers.size();i++)
							{StDevVals.add(Math.pow(numbers.elementAt(i)-average,2));}
							Double StDev = Math.sqrt(Average(StDevVals));
							
							
				// calculating threshold
							
				 threshold = average - number_stdev*StDev;}
				
			return threshold;
				
			};
			
*///--------------------------------------THRESHOLDS FROM SLA------------------------------------------------
	
// getting number of standard deviations threshold given SLA for case duration - Median; logarithms of case durations
/*			
	public Double getMedianStDevThreshold (XLog log, Double distribution_param, Double SLA_dur){
							
					Vector<Double> durlogs = new Vector<Double>();				
					Vector<Double> MadVals = new Vector<Double>();
					
					// getting logarithms of case durations
								for (XTrace t : log) {
									
									XAttributeDiscrete trace_duration = (XAttributeDiscrete)t.getAttributes().get("time:duration");
									long t_duration = trace_duration.getValue();
									durlogs.add(Math.log10(t_duration));
													
								}
								
								Collections.sort(durlogs);

					// calculating statistics and threshold	    
								
								for(int z=0;z<durlogs.size();z++)
								{MadVals.add(Math.abs(durlogs.elementAt(z)-Median(durlogs)));}
								Collections.sort(MadVals);
								Double MAD = Median(MadVals);
								Double threshold = (Math.log10(SLA_dur) - Median(durlogs))/(distribution_param*MAD);

					
				return threshold;
					
				};
*///------------------------------------------------------------------------------------------------------------				
// getting number of standard deviations threshold given SLA for case duration - Average; case durations
/*				
	public Double getAverageStDevThreshold (XLog log, Double distribution_param, Double SLA_dur){
					
					Vector<Double> durations = new Vector<Double>();				
					Vector<Double> StDevVals = new Vector<Double>();
					
					// getting logarithms of case durations
								for (XTrace t : log) {
									
									XAttributeDiscrete trace_duration = (XAttributeDiscrete)t.getAttributes().get("time:duration");
									long t_duration = trace_duration.getValue();
									Double duration = (double)t_duration;
									durations.add(duration);
								}
						
					// calculating standard deviation	    
									Double average = Average(durations);
									for(int z=0;z<durations.size();z++)
									{StDevVals.add(Math.pow(durations.elementAt(z)-average,2));}
									Double StDev = Math.sqrt(Average(StDevVals));
									
									
					// calculating threshold
									
				Double threshold = (SLA_dur - average)/(distribution_param*StDev);

						
					return threshold;
						
					};

*///-------------------------------------------------------------------------------------------------------				
// returns % of cases with violated time SLA
/*
	public Double getNumViolated (UIPluginContext context, XLog log, Double SLA_dur){
						
					int numTrace = log.size();
					int numViolated = 0;
					
								
					for (XTrace t : log) {
									XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
									XAttributeDiscrete trace_duration = (XAttributeDiscrete)trace.getAttributes().get("time:duration");
									long duration = trace_duration.getValue();
									if (duration > SLA_dur){numViolated+=1;};
													
								}
						
						Double threshold = (double)numViolated/numTrace;		
											
				return threshold;
					
				};
				
*///--------------------------------------------------------------------------------------------------------
// getting number of resources per case threshold (only for good cases)
/*				
	public Double getCaseResourcesAverageThreshold (XLog log, Double numStDev){
					
					Vector<Double> numResources = new Vector<Double>();	
					Vector<Double> StDevVals = new Vector<Double>();
					
					// getting number of case resources
					
					for (XTrace t : log) {
						
						XAttributeMap am = t.getAttributes();
						String caseQuality = am.get("outcome:case_quality").toString();
					if (caseQuality.equals("false")){	
						ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();
						
					for (XEvent e : t) 
						{
						
						String resource = XOrganizationalExtension.instance().extractResource(e);
						
							resources.add(resource);
						
						}
					double num = resources.size();
					numResources.add(num);
					}}
						
					// calculating standard deviation	    
					
									Double average = Average(numResources);
									for(int z=0;z<numResources.size();z++)
									{StDevVals.add(Math.pow(numResources.elementAt(z)-average,2));}
									Double StDev = Math.sqrt(Average(StDevVals));
									
									
					// calculating threshold
									
						Double threshold = average+numStDev*StDev;
				
					return threshold;
						
	};
	
*///--------------------------------------GETTING SLAs------------------------------------------------
/*	public Double getMedianLogSLASplit (XLog log, Double distribution_param, Double StDev){
		
		int i = 0;
		int splitIndex = 0;
		int size = log.size();
		
		if( (size % 2) == 0){splitIndex = size/2;}else{splitIndex = (size+1)/2;}	
				
		Vector<Double> durlogs = new Vector<Double>();				
		Vector<Double> MadVals = new Vector<Double>();
		
		// getting logarithms of case durations
					for (XTrace t : log) {
						if(i<splitIndex){
						XAttributeDiscrete trace_duration = (XAttributeDiscrete)t.getAttributes().get("time:duration");
						long t_duration = trace_duration.getValue();
						durlogs.add(Math.log10(t_duration));}i++;
										
					}
					
					Collections.sort(durlogs);

		// calculating statistics and threshold	    
					
					for(int z=0;z<durlogs.size();z++)
					{MadVals.add(Math.abs(durlogs.elementAt(z)-Median(durlogs)));}
					Collections.sort(MadVals);
					Double MAD = Median(MadVals);
					Double SLA = Median(durlogs)+StDev*distribution_param*MAD;

		
	return java.lang.Math.pow(10,SLA);
		
	};
	
*///-------------------------------------------------------------------------------------------------
// getting SLA given number of standard deviations - Median; logarithms of case durations
				

	//------------------------------------------------------------------------------------------------------------
	// getting SLA given number of standard deviations - Median; 
/*					
					public Double getMedianSLA (XLog log, Double distribution_param, Double StDev){
											
									Vector<Double> durlogs = new Vector<Double>();				
									Vector<Double> MadVals = new Vector<Double>();
									
									// getting case durations
												for (XTrace t : log) {
													
													XAttributeDiscrete trace_duration = (XAttributeDiscrete)t.getAttributes().get("time:duration");
													long t_duration = trace_duration.getValue();
													Double duration = (double)t_duration;
													durlogs.add(duration);
																	
												}
												
												Collections.sort(durlogs);

									// calculating statistics and threshold	    
												
												for(int z=0;z<durlogs.size();z++)
												{MadVals.add(Math.abs(durlogs.elementAt(z)-Median(durlogs)));}
												Collections.sort(MadVals);
												Double MAD = Median(MadVals);
												Double SLA = Median(durlogs)+StDev*distribution_param*MAD;

									
								return SLA;
									
								};
	//------------------------------------------------------------------------------------------------------------				
*/	// getting SLA for case duration given number of standard deviations- Average; case durations
/*					
		public Double getAverageSLA (XLog log, Double StDevNum){
						
						Vector<Double> durations = new Vector<Double>();				
						Vector<Double> StDevVals = new Vector<Double>();
						
						// getting case durations
									for (XTrace t : log) {
										
										XAttributeDiscrete trace_duration = (XAttributeDiscrete)t.getAttributes().get("time:duration");
										long t_duration = trace_duration.getValue();
										Double duration = (double)t_duration;
										durations.add(duration);
									}
							
						// calculating standard deviation	    
										Double average = Average(durations);
										for(int z=0;z<durations.size();z++)
										{StDevVals.add(Math.pow(durations.elementAt(z)-average,2));}
										Double StDev = Math.sqrt(Average(StDevVals));
										
										
						// calculating threshold
										
					Double SLA = average+StDevNum*StDev;

							
						return SLA;
							
						};
						
*/	//------------------------------------------------------------------------------------------------------------				
	// getting SLA for case duration given number of standard deviations- Average; case durations
/*										
				public Double getAverageLogSLA (XLog log, Double StDevNum){
											
								Vector<Double> durations = new Vector<Double>();				
								Vector<Double> StDevVals = new Vector<Double>();
											
								// getting case durations
									for (XTrace t : log) {
															
										XAttributeDiscrete trace_duration = (XAttributeDiscrete)t.getAttributes().get("time:duration");
										long t_duration = trace_duration.getValue();
										durations.add(Math.log10(t_duration));
										}
												
								// calculating standard deviation	    
										Double average = Average(durations);
										for(int z=0;z<durations.size();z++)
										{StDevVals.add(Math.pow(durations.elementAt(z)-average,2));}
										Double StDev = Math.sqrt(Average(StDevVals));
															
														
								// calculating threshold
															
						Double SLA = average+StDevNum*StDev;

												
						return java.lang.Math.pow(10,SLA);
												
											};
	
*/
					



