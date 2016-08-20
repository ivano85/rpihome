package com.github.ivano85.rpihome.wm;

import java.lang.reflect.Field;

import gnu.x11.Atom;
import gnu.x11.Data;
import gnu.x11.Window;
import gnu.x11.Window.Property;
import gnu.x11.Window.WMSizeHints;

public class Client {
	
	private final Window window;
	private final WindowType type;

	public Client(Window window) {
		
		super();
		
		Field propField = null;
		try {
			propField = Property.class.getDeclaredField("value");
		} catch (NoSuchFieldException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		this.window = window;
		
		// Window classification
		int value = readAtom("_NET_WM_WINDOW_TYPE");
		Atom atom = (Atom) Atom.intern(window.display, value);
		if (atom != null) {
			if ("_NET_WM_WINDOW_TYPE_NORMAL".equals(atom.name)) {
				type = WindowType.NORMAL;
			}
			else if ("_NET_WM_WINDOW_TYPE_DIALOG".equals(atom.name)) {
				type = WindowType.DIALOG;
			}
			else if ("_NET_WM_WINDOW_TYPE_DOCK".equals(atom.name)) {
				type = WindowType.DOCK;
			}
			else if ("_NET_WM_WINDOW_TYPE_SPLASH".equals(atom.name)) {
				type = WindowType.SPLASH;
			}
			else if ("_NET_WM_WINDOW_TYPE_DESKTOP".equals(atom.name)) {
				type = WindowType.DESKTOP;
			}
			else {
				type = WindowType.NORMAL;
			}
		}
		else {
			type = WindowType.NORMAL;
		}
		
		// Window properties
		Atom[] props = window.properties();
		for (Atom a : props) {
			System.out.print(a.id + ": " + a.name + ": ");
			Property p = window.get_property(false, a, Atom.ANY_PROPERTY_TYPE, 0, 100);
			System.out.print("[" + p.format() + "] ");
			int n = p.string_value().length() * 8 / p.format();
			int[] data = new int[n];
			for (int i = 0; i < n; i++) {
				if (i > 0) {
					System.out.print(", ");
				}
				data[i] = readProperty(p, i);
				System.out.print(data[i]);
			}
			System.out.println();
			if (p.format() == 8) {
				System.out.println(a.id + ": " + a.name + ": (string) " + p.string_value());
			}
			// Parse
			if ("WM_NORMAL_HINTS".equals(a.name)) {
				WMSizeHints hints = new WMSizeHints(new Data(data, p.format() / 8));
				hints.min_height();
			}
		}
		
	}

	public Window getWindow() {
		return window;
	}

	public WindowType getType() {
		return type;
	}
	
	public int readAtom(String name) {
		
		Atom prop = Atom.intern(window.display, name, true);
		Property wtype = window.get_property(false, prop, Atom.ATOM, 0, 1);
		
		return readProperty(wtype, 0);
		
	}
	
	public int readProperty(Property p, int index) {
		
		int value = p.value(index);
		
		// Bug
		if (p.format() == 32) {
			try {
				
				Field propField = p.getClass().getDeclaredField("value");
				propField.setAccessible(true);
				byte[] obj = (byte[]) propField.get(p);
				value =   ((0xff & obj[index * 4]) << 24)
			            | ((0xff & obj[index * 4 + 1]) << 16)
			            | ((0xff & obj[index * 4 + 2]) << 8)
			            |  (0xff & ((int) obj[index * 4 + 3]));
				
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// ...
		
		return value;
		
	}
	
}
