package com.github.ivano85.rpihome.wm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gnu.x11.Window;

public class WindowsStore {
	
	private List<Client> windows = new LinkedList<Client>();
	private Map<Integer, Client> byWindowIds = new HashMap<Integer, Client>();
	
	public Client findByWindowId(int id) {
		return byWindowIds.get(id);
	}
	
	public synchronized Client store(Window window) {
		Client client = new Client(window);
		windows.add(client);
		byWindowIds.put(window.id, client);
		return client;
	}

	public synchronized boolean destroy(int window_id) {
		
		boolean redoLayout = false;
		
		byWindowIds.remove(window_id);
		Iterator<Client> iter = windows.iterator();
		while (iter.hasNext()) {
			Client c = iter.next();
			if (c.getWindow().id == window_id) {
				if (c.getType() == WindowType.DOCK) {
					redoLayout = true;
				}
				iter.remove();
			}
		}
		
		return redoLayout;
		
	}
	
}
