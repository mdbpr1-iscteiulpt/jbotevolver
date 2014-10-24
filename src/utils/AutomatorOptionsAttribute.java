package utils;

import java.awt.Component;

import javax.swing.JCheckBox;

public class AutomatorOptionsAttribute {

	String name;
	Component component;
	String defaultValue;
	JCheckBox checkBox;
	
	public AutomatorOptionsAttribute() { }
	
	public AutomatorOptionsAttribute(String name, Component component, String defaultValue) {
		this.name = name;
		this.component = component;
		this.defaultValue = defaultValue;
	}
	
	public String getName() {
		return name;
	}
	
	public Component getComponent() {
		return component;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public void setComponent(Component component) {
		this.component = component;
	}
	
	public JCheckBox getCheckBox() {
		return checkBox;
	}
	
	public void setCheckBox(JCheckBox checkBox) {
		this.checkBox = checkBox;
	}
	
}
