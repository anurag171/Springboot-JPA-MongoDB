package com.anurag.spring.mongodb;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.springframework.stereotype.Component;

@Component
public class PropertyUtil implements PropertyChangeListener {
	
	
	/*
	 * public void firePropertyChange(String propertyName,int oldValue,int
	 * newValue){ EventListener[]
	 * listeners=listenerList.getListeners(PropertyChangeListener.class); if
	 * (listeners.length == 0) { return; } PropertyChangeEvent evt=new
	 * PropertyChangeEvent(this,propertyName,new Integer(oldValue),new
	 * Integer(newValue)); for (int i=0; i < listeners.length; i++) {
	 * PropertyChangeListener l=(PropertyChangeListener)listeners[i];
	 * l.propertyChange(evt); } }
	 */

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		  System.out.println("Name      = [{}]"+ evt.getPropertyName());
		  System.out.println("Old Value = [{}]"+evt.getOldValue());
		  System.out.println("New Value = [{}]"+ evt.getNewValue());
	}

}
