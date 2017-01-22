package org.processmining.processriskindicators.inout;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.widgets.ProMTextField;

import com.fluxicon.slickerbox.components.HeaderBar;
import com.fluxicon.slickerbox.components.SlickerButton;

@SuppressWarnings("serial")
public class GetInputParameters extends JDialog{


@SuppressWarnings({ "rawtypes", "unchecked" })
public InputParameters defineOneLogParams(final InputParameters ip) throws Exception
{
	final HeaderBar pane = new HeaderBar("");	
	setContentPane(pane);
	getRootPane().setBorder( BorderFactory.createLineBorder(Color.GRAY) );
	pane.setLayout(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	c.fill = GridBagConstraints.CENTER;
		

final JLabel defVarsText=new JLabel();
defVarsText.setText("<html><h3>PRI parameters: </h3></html>");
defVarsText.setForeground(UISettings.TextLight_COLOR);
c.ipadx = 200;      
c.gridwidth = 2;
c.gridx = 0;
c.gridy = 0;
pane.add(defVarsText, c);

final JLabel lab7 = new JLabel("Normal case duration (int):");  
lab7.setForeground(UISettings.TextLight_COLOR);
c.ipadx = 200; 
c.gridwidth = 2;
c.gridx = 0;
c.gridy = 1;
pane.add(lab7, c);

final ProMTextField tslotnum = new ProMTextField("1");
c.gridwidth = 1;
c.gridx = 0;
c.gridy = 2;
c.ipadx = 100;
pane.add(tslotnum, c);

 DefaultComboBoxModel model = new DefaultComboBoxModel();
 model.addElement("days");
 model.addElement("weeks");
 model.addElement("hours");
 model.addElement("months");
 model.addElement("minutes");
 model.addElement("seconds");
 model.addElement("years");

 final ProMComboBox tslotunit = new ProMComboBox(model);
c.gridwidth = 1;
c.gridx = 1;
c.gridy = 2;
c.ipadx = 100;
pane.add(tslotunit, c);

//-------------------------------------------------------------------------
	final JLabel tsoutmethlab=new JLabel();
	tsoutmethlab.setText("<html><h3>Configure PRIs? </h3></html>");
	tsoutmethlab.setForeground(UISettings.TextLight_COLOR);
	c.ipadx = 100;      
	c.gridwidth = 1;
	c.gridx = 0;
	c.gridy = 3;
	pane.add(tsoutmethlab, c);	
			
		
		 DefaultComboBoxModel model1 = new DefaultComboBoxModel();
	     model1.addElement("Yes");
	     model1.addElement("No");
	    	   
	    final ProMComboBox confPRI = new ProMComboBox(model1);
	    c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 3;
		c.ipadx = 100;
		pane.add(confPRI, c);

//---------------------------------------------------------
		final JLabel lab1 = new JLabel("Desired precision level: ");  
		lab1.setForeground(UISettings.TextLight_COLOR);
		c.ipadx = 100; 
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 4;
		pane.add(lab1, c);
		
		final ProMTextField prec = new ProMTextField("0.90");
		c.gridx = 1;
		c.gridy = 4;
		c.ipadx = 100;
		pane.add(prec, c);
		

//-----------------------------------------------------------------------------

		final JLabel calcSLAlab=new JLabel();
		calcSLAlab.setText("<html><h3>Derive normal case duration? </h3></html>");
		calcSLAlab.setForeground(UISettings.TextLight_COLOR);
		c.ipadx = 100;      
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 5;
		pane.add(calcSLAlab, c);	
				
			
			 DefaultComboBoxModel model2 = new DefaultComboBoxModel();
		     model2.addElement("No");
		     model2.addElement("Yes");
		    	   
		    final ProMComboBox calcSLA = new ProMComboBox(model2);
		    c.gridwidth = 1;
			c.gridx = 1;
			c.gridy = 5;
			c.ipadx = 100;
			pane.add(calcSLA, c);

		
		
SlickerButton but=new SlickerButton();
but.setText("Submit");
c.gridx = 0;
c.gridy = 6;
c.gridwidth = 2;
pane.add(but, c);
  
 	
   but.addActionListener(
           new ActionListener(){
               public void actionPerformed(
                       ActionEvent e) {
            	   
            	 ip.desiredPrecision = Double.parseDouble(prec.getText());
            	 String confMethod = (String) confPRI.getSelectedItem();
            	 if(confMethod.equals("Yes"))
            	 {ip.configure = true;}else
            	 {ip.configure = false;}
            	 
            	 String calcSLAq = (String) calcSLA.getSelectedItem();
            	 if(calcSLAq.equals("Yes"))
            	 {ip.calculateSLA = true;}else
            	 {ip.calculateSLA = false;}
            
            	   
            	   long unit = 0;
            	   int slotsize = Integer.parseInt(tslotnum.getText());
            	   String slotunit = (String) tslotunit.getSelectedItem();
            	   if(slotunit.equals("years")){unit = (604800000l/7)*365;}//365 days 
            	   else if(slotunit.equals("months")){unit = (604800000l/7)*30;}//30 days 
            	   else if(slotunit.equals("weeks")){unit = 604800000l;}
            	   else if(slotunit.equals("days")){unit = 604800000l/7;}
            	   else if(slotunit.equals("hours")){unit = 604800000l/(7*24);}
            	   else if(slotunit.equals("minutes")){unit = 604800000l/(7*24*60);}
            	   else if(slotunit.equals("seconds")){unit = 604800000l/(7*24*60*60);}
            	   
            	   
            	   ip.SLA = (double) (unit*slotsize);
            
              	   dispose(); }
                               }
                       );
   
   setSize(700,300);
   setModal(true);
   setLocationRelativeTo(null);
   setVisible(true);
   setDefaultCloseOperation(DISPOSE_ON_CLOSE);
   
   return ip;
} 

		

}

