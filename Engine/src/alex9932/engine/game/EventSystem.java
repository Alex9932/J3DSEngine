package alex9932.engine.game;

import java.util.ArrayList;

// Simple event system

public class EventSystem {
	private ArrayList<IEventHandler> eventHandlers = new ArrayList<IEventHandler>();
	
	public EventSystem() {
		
	}
	
	public void addEventHandler(IEventHandler handler) {
		eventHandlers.add(handler);
	}
	
	public void sendSignal(Event event) {
		//System.out.println(event);
		for (int i = 0; i < eventHandlers.size(); i++) {
			//System.out.println(eventHandlers.get(i).getClass().getName());
			eventHandlers.get(i).handle(event);
		}
	}
}