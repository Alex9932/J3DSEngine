package alex9932.utils;

import java.util.HashMap;

public class Profiler {
	private HashMap<String, Long> selections;
	private String currentSelection;
	private long startTime;
	
	public Profiler() {
		this.selections = new HashMap<String, Long>();
		this.currentSelection = "null";
	}
	
	public void startSelection(String selection) {
		endTimer(this.currentSelection);
		this.currentSelection = selection;
		startTimer();
	}

	private void startTimer() {
		this.startTime = System.nanoTime();
	}

	private void endTimer(String selection) {
		long time = System.nanoTime() - this.startTime;
		this.selections.put(selection, time);
	}
	
	public String getTime(String selection) {
		try{
			return "" + (this.selections.get(selection) / 1000000) + "ms";
		}catch(Exception e){
			this.selections.put(selection, 0L);
			return "";
		}
	}
}