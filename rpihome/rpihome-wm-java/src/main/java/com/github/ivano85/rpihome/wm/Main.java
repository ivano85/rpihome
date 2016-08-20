package com.github.ivano85.rpihome.wm;

import java.io.File;
import java.io.IOException;

import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import gnu.x11.Atom;
import gnu.x11.Color;
import gnu.x11.Display;
import gnu.x11.Error;
import gnu.x11.Input;
import gnu.x11.Input.InputFocusInfo;
import gnu.x11.Option;
import gnu.x11.Window;
import gnu.x11.Window.Changes;
import gnu.x11.event.ClientMessage;
import gnu.x11.event.ConfigureRequest;
import gnu.x11.event.CreateNotify;
import gnu.x11.event.DestroyNotify;
import gnu.x11.event.Event;
import gnu.x11.event.MapRequest;
import gnu.x11.event.ReparentNotify;

public class Main {
	
	private final WindowsStore clients = new WindowsStore();
	private final LayoutManager layout = new LayoutManager(clients);
	private final Display display;
	private Window rootWindow;
	
	public Main (String [] args) throws IOException {
		
		Option option = new Option (args);

		// cast `gnu.util.Option option' to `gnu.x11.Option'
		String env = gnu.util.Environment.value ("DISPLAY");
		Display.Name display_name = option.display_name ("display",
				"X server to connect to", new Display.Name (env));

		if (option.flag ("help", "display this help screen and exit")) {
			display = null;
			return;
		}
		else if (option.flag("tcp", "use tcp sockets instead of unix sockets")) {
			
			System.out.println("Using TCP socket " + display_name.hostname + ":" + (6000 + display_name.display_no));
			
			display = new Display(display_name.hostname, display_name.display_no, display_name.screen_no);
			
		}
		else {
			
			File socketFile = new File("/tmp/.X11-unix/X" + display_name.display_no);
			
			System.out.println("Using UNIX socket " + socketFile.getAbsolutePath());
			
			AFUNIXSocket sock = AFUNIXSocket.newInstance();
			sock.connect(new AFUNIXSocketAddress(socketFile));
			
			display = new Display (sock, display_name.hostname, display_name.display_no, display_name.screen_no);
			
		}
		
	}

	public static void main(String[] args) throws IOException {

		Main main = new Main(args);

		Display d = main.display;

		d.set_close_down_mode(Display.DESTROY);
		System.out.println("Found " + d.screens.length + " screens");
		
		main.rootWindow = d.default_root;
		Input input = d.input;
		
		main.rootWindow.select_input(Event.SUBSTRUCTURE_REDIRECT_MASK | Event.SUBSTRUCTURE_NOTIFY_MASK);
		
		main.rootWindow.set_background(new Color(0xFFFFFF));
		
		InputFocusInfo info = input.input_focus();
		//info.focus.set_input_focus();
		
		main.display.flush();

		while (true) {

			Event event = main.display.next_event();
			
			System.out.println("Event type: " + event.getClass().getName());
			
			if (event instanceof ConfigureRequest) {
				main.onConfigureRequest((ConfigureRequest) event);
			}
			else if (event instanceof MapRequest) {
				main.onMapRequest((MapRequest) event);
			}
			else if (event instanceof CreateNotify) {
				main.onCreateNotify((CreateNotify) event);
			}
			else if (event instanceof DestroyNotify) {
				main.onDestroyNotify((DestroyNotify) event);
			}
			else if (event instanceof ReparentNotify) {
				main.onReparentNotify((ReparentNotify) event);
			}
			else if (event instanceof ClientMessage) {
				main.onClientMessage((ClientMessage) event);
			}

		}

	}
	
	private void onConfigureRequest(ConfigureRequest evt) {
		
		Client client = clients.findByWindowId(evt.window_id);
		
		if (client == null) {
			try {
				for (Window w : rootWindow.tree().children()) {
					if (w.id == evt.window_id) {
						client = clients.store(w);
					}
				}
			} catch (Error error) {
				error.printStackTrace();
			}
		}
		
		if (client == null) {
			return;
		}
		
		Changes changes = new Changes();
		
		if (client.getType() == WindowType.NORMAL) {
			
			changes.width(rootWindow.screen().width);
			changes.height(rootWindow.screen().height);
			changes.x(0);
			changes.y(0);
			changes.stack_mode(Changes.ABOVE);
			
		}
		else if (client.getType() == WindowType.DOCK) {
			
			changes.width(rootWindow.screen().width);
			changes.x(0);
			changes.y(0);
			
			// TODO: Redo layout
			layout.layoutWindows();
			
		}
		else if (client.getType() == WindowType.DIALOG || client.getType() == WindowType.SPLASH) {
			
			changes.x((rootWindow.screen().width - client.getWindow().width) / 2);
			changes.y((rootWindow.screen().height - client.getWindow().height) / 2);
			changes.stack_mode(Changes.ABOVE);
			
		}
		else if (client.getType() == WindowType.DESKTOP) {
			// TODO: Desktop layout...
		}
		
		client.getWindow().configure(changes);
		
	}
	
	private void onMapRequest(MapRequest evt) {
		
		Client client = clients.findByWindowId(evt.window_id);
		
		if (client == null) {
			for (Window w : rootWindow.tree().children()) {
				if (w.id == evt.window_id) {
					client = clients.store(w);
				}
			}
		}
		
		if (client == null) {
			return;
		}
		
		client.getWindow().map();
		
	}
	
	private void onCreateNotify(CreateNotify evt) {
		
	}
	
	private void onDestroyNotify(DestroyNotify evt) {
		if (clients.destroy(evt.window_id)) {
			layout.layoutWindows();
		}
	}
	
	private void onReparentNotify(ReparentNotify evt) {
		
	}
	
	private void onClientMessage(ClientMessage evt) {
		
	}

}
