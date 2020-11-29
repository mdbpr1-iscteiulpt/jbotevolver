package gui.util;

import java.awt.Color;
import java.io.Serializable;

public class AltimetryColor implements Serializable {
	private Color[] ColorRamp =
				{new Color(179,179,179), new Color(141, 172,148),
				 new Color(109,163,122), new Color(82, 153,89), 
				 new Color(79,151,70), new Color(107, 176,87), 				
				 new Color(161,203,102), new Color(213, 223,119), 				
				 new Color(249,243,132), new Color(232, 219,115), 
				 new Color(216,196,102), new Color(205, 172,89), 
				 new Color(194,152,80), new Color(179, 138,72), 
				 new Color(168,128,71), new Color(152, 114,66), 				 
				 new Color(143,104,64), new Color(131, 93,61), 	
				 new Color(121,80,58), new Color(107, 65,55),
				 new Color(94, 56,51)
				};
	
	public AltimetryColor() {
		super();
		
	}
	
	public Color getColorinLayer(int numb) {
		return ColorRamp[numb];
	}
	
	
	
	@Override
	public String toString() {
		return "AltimetryColor";
	}


}
