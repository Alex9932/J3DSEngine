package alex9932.script;

import java.util.HashMap;
import java.util.Iterator;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.openal.AL;

import alex9932.engine.game.Display;
import alex9932.engine.game.Engine;
import alex9932.engine.render.GL;
import alex9932.utils.sound.SoundSystem;

public class ScriptsEngine {
	private static final String SCRIPTS_PATH = "gamedata/scripts/";
	private static final String SCRIPTS_CONFIG = "gamedata/scripts/scripts.json";
	
	private HashMap<String, Script> scripts = new HashMap<String, Script>();

	private Json json = new Json();
	private Sound soundSys = new Sound();
	private GL gl;
	private Engine engine;
	
	public ScriptsEngine(Engine engine) {
		this.engine = engine;
		this.gl = new GL();
		System.out.println("[Engine] Starting up...");
		ScriptEngine js = new ScriptEngineManager().getEngineByName("javascript");
		System.out.println("[Engine] " + js.getFactory().getEngineName() + " v" + js.getFactory().getEngineVersion());
		System.out.println("[Engine] " + js.getFactory().getLanguageName() + " " + js.getFactory().getLanguageVersion());

		AL.setCurrentThread(SoundSystem.getCaps());
		
		try {
			JSONObject obj = new JSONObject(FileIO.read(SCRIPTS_CONFIG));
			JSONArray array = obj.getJSONArray("scripts");
			for (int i = 0; i < array.length(); i++) {
				Script script = new Script(this, FileIO.read(SCRIPTS_PATH + array.getString(i)));
				
				Display.getEventSystem().addKeyListener(script);
				Display.getEventSystem().addMouseListener(script);
				engine.getEventSystem().addEventHandler(script);
				scripts.put(array.getString(i), script);
			}
			for (int i = 0; i < array.length(); i++) {
				scripts.get(array.getString(i)).construct();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void loadLevel(String level) {
		engine.loadLevel(level);
	}
	
	public int getFps() {
		return Display.getFps();
	}
	
	public Json getJson() {
		return json;
	}
	
	public GL getGL() {
		return gl;
	}
	
	public Sound getSoundSys(){
		return soundSys;
	}
	
	public Engine getEngine() {
		return this.engine;
	}
	
	public Script get(String name) {
		return scripts.get(name);
	}
	
	public void destroy() throws Exception {
		Iterator<Script> iterator = scripts.values().iterator();
		for (int i = 0; i < scripts.size(); i++) {
			Script s = iterator.next();
			s.destroy();
		}
	}
}