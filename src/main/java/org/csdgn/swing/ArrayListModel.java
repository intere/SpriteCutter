package org.csdgn.swing;

import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.Icon;

public class ArrayListModel implements ListModel<Icon> {
	private ArrayList<Icon> list = new ArrayList<>();
	private HashSet<ListDataListener> listeners = new HashSet<>();
	
	public void add(Icon obj) {
		list.add(obj);
		int pos = list.size() - 1;
		fireAddChange(new ListDataEvent(this,ListDataEvent.INTERVAL_ADDED,pos,pos));
	}
	
	public void remove(int index) {
		list.remove(index);
		fireRemoveChange(new ListDataEvent(this,ListDataEvent.INTERVAL_REMOVED,index,index));
	}
	
	public void remove(Object obj) {
		remove(list.indexOf(obj));
	}
	
	public void clear() {
		if(list.isEmpty())
			return;
		
		int n = list.size()-1;
		list.clear();
		fireRemoveChange(new ListDataEvent(this,ListDataEvent.INTERVAL_REMOVED,0,n));
	}
	
	private void fireAddChange(ListDataEvent lde) {
		for(ListDataListener l : listeners) {
			l.intervalAdded(lde);
		}
	}
	
	private void fireRemoveChange(ListDataEvent lde) {
		for(ListDataListener l : listeners) {
			l.intervalRemoved(lde);
		}
	}
	
	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public Icon getElementAt(int index) {
		return list.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}
}
