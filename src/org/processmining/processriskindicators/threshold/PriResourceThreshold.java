package org.processmining.processriskindicators.threshold;

import java.util.Vector;


public class PriResourceThreshold {

	public String resource;
	public Vector<PriResourceActivityThreshold> resourceActivities = new Vector<PriResourceActivityThreshold>();
	
	public Vector<Long> resourceWorkloads = new Vector<Long>();
	public Long current_workload;
	
	//-----
	public Vector<Long> ActiveWorkloads = new Vector<Long>();
	public Vector<Long> times = new Vector<Long>();
	public Long current_workload2;

	
	
	public Double PRI9av;
	public Double PRI9dev;
		
	public Double PRI9threshold;
		
	//-----------------for outcome learning---------------------------------
	//-----
	public Vector<Integer>  pri9true = new Vector<Integer>();
	public Vector<Integer>  pri9false = new Vector<Integer>();
	public Vector<Double>  pri9fraction = new Vector<Double>();
	
	public int [] pri9t;
	public int [] pri9f;
	public double [] pri9part;
	
	public int pri9t1t;
	public int pri9t1f;
	public int pri9t2t;
	public int pri9t2f;
	public int pri9t3t;
	public int pri9t3f;
	
	
	//--------------------------------------------------------------------
	
	public PriResourceThreshold (String res, int devNum, double step){
		
		resource = res;
		PRI9av = 0.00;
		PRI9dev = 0.00;
		current_workload = new Long(0);
		current_workload2 = new Long(0);
			
		PRI9threshold = 0.00;
		
		pri9t = new int [devNum+1];
		pri9f = new int [devNum+1];
		pri9part = new double [devNum+1];


		for(int i=0;i<(devNum+1);i++)
		{
			pri9t[i]=0;
			pri9f[i]=0;
			pri9part[i]=0.00;
		}
		
		 pri9t1t = 0;
		 pri9t1f = 0;
		 pri9t2t = 0;
		 pri9t2f = 0;
		 pri9t3t = 0;
		 pri9t3f = 0;
		 
		//----
		 int stdDevSize = (int) (devNum/step) + 1;
		 for(int i=0; i<stdDevSize; i++)
			{
				pri9true.add(0);
				pri9false.add(0);
				pri9fraction.add(0.0);
				
			}
		 
 		}
	
  public int getIndex(String name){
	  
	  int index = -1;
	  
	  for (int i=0;i<resourceActivities.size();i++){
		  
		  if(resourceActivities.elementAt(i).actName.equals(name)){index = i;}
		  
	  }
	  return index;
  }
  
 public int getTimeIndex(Long d){
	  
	  int index = -1;
	  
	  for (int i=0;i<times.size();i++){
		  
		  if(times.elementAt(i).equals(d)){index = i;}
		  
	  }
	  return index;
  }
}

