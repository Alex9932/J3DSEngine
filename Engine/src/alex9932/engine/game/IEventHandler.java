package alex9932.engine.game;


public interface IEventHandler {
	public void onLoadEvent(String level);
	public void startLoadLevelEvent(String level);
	public void endLoadLevelEvent(String level);
	public void startupEvent();
	public void shutdownEvent();
	public void handle(Event event);
}