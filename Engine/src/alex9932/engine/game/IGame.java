package alex9932.engine.game;

public interface IGame {
	public void startup() throws Exception;
	public void update();
	public void shutdown();
	public void onLevelLoaded(String level);
	public void onStartLevelLoading(String level);
}