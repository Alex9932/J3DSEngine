package alex9932.engine.game;


public interface IEventHandler {
	public void onLoadEvent(String level);
	public void startLoadLevelEvent(String level);
	public void endLoadLevelEvent(String level);
	public void startupEvent(String level);
	public void shutdownEvent(String level);
	public void handle(Event event);
}