package org.processmining.processriskindicators.threshold;

import java.util.Vector;


public class PriCaseThreshold {

	public Vector<PriActivityThreshold> activities = new Vector<PriActivityThreshold>();
	public Vector<PriResourceThreshold> resources = new Vector<PriResourceThreshold>();
	
	public Vector<Long> caseResources = new Vector<Long>();
	public Vector<Long> caseActivities = new Vector<Long>();
	
	public Double PRI5av;
	public Double PRI5dev;
	public Double PRI7av;
	public Double PRI7dev;
	
	public Double PRI5threshold;
	public Double PRI7threshold;
	
	
	//---------
	
	public Vector<Double>  stdDev = new Vector<Double>();
		
	public Vector<Integer>  pri5true = new Vector<Integer>();
	public Vector<Integer>  pri5false = new Vector<Integer>();
	public Vector<Double>  pri5fraction = new Vector<Double>();
	
	public Vector<Integer>  pri7true = new Vector<Integer>();
	public Vector<Integer>  pri7false = new Vector<Integer>();
	public Vector<Double>  pri7fraction = new Vector<Double>();

	
	//-----------------for outcome learning---------------------------------
	
	public int [] pri5t;
	public int [] pri5f;
	public double [] pri5part;

	public int [] pri7t;
	public int [] pri7f;
	public double [] pri7part;

	public int pri5t1t;
	public int pri5t1f;
	public int pri5t2t;
	public int pri5t2f;
	public int pri5t3t;
	public int pri5t3f;
	
	public int pri7t1t;
	public int pri7t1f;
	public int pri7t2t;
	public int pri7t2f;
	public int pri7t3t;
	public int pri7t3f;
	
	//--------------------------------------------------------------------
	
	public PriCaseThreshold (int devNum, double step){
		
		//----
		
		for(double j=0.0; j<=devNum; j+=step)
		stdDev.add(j);
		
		
		for(int i=0; i<stdDev.size(); i++)
		{
			pri5true.add(0);
			pri5false.add(0);
			pri5fraction.add(0.0);
			
			pri7true.add(0);
			pri7false.add(0);
			pri7fraction.add(0.0);
		}
		
		//--
		
		
		PRI5av = 0.00;
		PRI5dev = 0.00;
		PRI7av = 0.00;
		PRI7dev = 0.00;
		
		PRI5threshold = 0.00;
		PRI7threshold = 0.00;
		
		pri5t = new int [devNum+1];
		pri5f = new int [devNum+1];
		pri5part = new double [devNum+1];

		pri7t = new int [devNum+1];
		pri7f = new int [devNum+1];
		pri7part = new double [devNum+1];


		for(int i=0;i<(devNum+1);i++)
		{
			pri5t[i]=0;
			pri5f[i]=0;
			pri5part[i]=0.00;
			
			pri7t[i]=0;
			pri7f[i]=0;
			pri7part[i]=0.00;

		}
		
		 pri5t1t = 0;
		 pri5t1f = 0;
		 pri5t2t = 0;
		 pri5t2f = 0;
		 pri5t3t = 0;
		 pri5t3f = 0;
		 
		 pri7t1t = 0;
		 pri7t1f = 0;
		 pri7t2t = 0;
		 pri7t2f = 0;
		 pri7t3t = 0;
		 pri7t3f = 0;
		
		}
	
  public int getIndex(String actName){
	  
	  int index = -1;
	  
	  for (int i=0;i<activities.size();i++){
		  
		  if(activities.elementAt(i).name.equals(actName)){index = i;}
		  
	  }
	  return index;
  }
  
public int getResourceIndex(String res){
	  
	  int index = -1;
	  
	  for (int i=0;i<resources.size();i++){
		  
		  if(resources.elementAt(i).resource.equals(res)){index = i;}
		  
	  }
	  return index;
  }
}

