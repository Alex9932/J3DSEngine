package alex9932.engine.game;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;

import alex9932.engine.physics.Body;
import alex9932.engine.physics.Material;
import alex9932.engine.physics.Physics;
import alex9932.engine.render.Renderer;
import alex9932.script.FileIO;
import alex9932.script.ScriptsEngine;
import alex9932.utils.FmlLoader;
import alex9932.utils.Profiler;
import alex9932.utils.Resource;
import alex9932.utils.Timer;
import alex9932.utils.gl.Vao;
import alex9932.utils.gl.Vbo;
import alex9932.utils.gl.texture.Texture;
import alex9932.utils.sound.SoundSystem;

public class Engine {
	public Timer timer;
	public Scene scene;
	public Renderer renderer;
	public Physics physics;
	public ScriptsEngine script;
	public IGame game;
	private EventSystem eventsys;
	private String level;
	private Profiler profiler;
	private boolean toLoad;
	private boolean running = true;
	
	public Engine(IGame game) {
		System.out.println("[Engine] Starting up...");
		this.game = game;
		this.profiler = new Profiler();
		timer = new Timer(1024);
		scene = new Scene();
		Display.create();
		Display.getDisplay().setDebug();
		System.out.println("[Engine] ~~~ OPENGL INFORMATION ~~~");
		System.out.println("[Engine] [OpenGL] Version: " + GL11.glGetString(GL11.GL_VERSION));
		System.out.println("[Engine] [OpenGL] GLSL: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
		eventsys = new EventSystem();
		script = new ScriptsEngine(this);
		eventsys.sendSignal(Event.STARTUP);
		SoundSystem.init();
		renderer = new Renderer();
		physics = new Physics();
	}
	
	public void run() throws Exception {
		game.startup();
		Display.getDisplay().getEventSystem().setGrabbed(true);
		while (running) {
			profiler.startSelection("timeupdate");
			if(Display.isCloseRequested()){
				running = false;
			}
			timer.updateTimer();
			profiler.startSelection("globalupdates");
			for (int i = 0; i < timer.elapsedTicks; ++i) {
				renderer.getCamera().update();
				game.update();
				physics.update();
				if(scene != null){
					for (int j = 0; j < scene.getObjects().size(); j++) {
						GameObject object = scene.getObject(j);
						object.update();
					}
				}
			}
			profiler.startSelection("render");
			renderer.render(scene);
			
			if(toLoad){
				profiler.startSelection("levelload");
				_loadLevel(level);
			}
			profiler.startSelection("NONE");
		}
		_shutdown();
	}
	
	private void _shutdown() {
		game.shutdown();
		physics.destroy();
		renderer.destroy();
		SoundSystem.cleanUp();
		Display.destroy();
	}

	public void shutdown() {
		eventsys.sendSignal(Event.SHUTDOWN);
		running = false;
	}

	public EventSystem getEventSystem() {
		return eventsys;
	}

	public void loadLevel(String level) {
		this.level = level;
		this.toLoad = true;
	}

	private void _loadLevel(String level) throws Exception {
		System.out.println("[Engine] Starting load level: " + level + "...");
		eventsys.sendSignal(Event.START_LOAD_LEVEL);
		eventsys.sendSignal(Event.ON_LOAD_EVENT);
		game.onStartLevelLoading(level);
		scene.destroy();
		physics.destroy();
		physics = new Physics();
		scene = null;
		scene = new Scene();
		JSONObject root = new JSONObject(FileIO.read(Resource.getLevel(level + "/entitys.json")));
		HashMap<String, Texture> textures = new HashMap<String, Texture>();
		HashMap<String, Vao> models = new HashMap<String, Vao>();
		
		System.out.println("[Engine] Loading geometry...");
		FmlLoader loader = new FmlLoader(Resource.getLevel(level + "/geometry.fml"));
		Vao terrainvao = new Vao();
		terrainvao.setIndices(loader.getInds());
		terrainvao.put(new Vbo(0, 3, loader.getVerts()));
		terrainvao.put(new Vbo(1, 2, loader.getTextureCoord()));
		terrainvao.put(new Vbo(2, 3, loader.getNormals()));
		
		JSONArray mdls = root.getJSONArray("models");
		for (int i = 0; i < mdls.length(); i++) {
			JSONObject mdl = mdls.getJSONObject(i);
			FmlLoader md = new FmlLoader(Resource.getModel(mdl.getString("file")));
			Vao vao = new Vao();
			vao.setIndices(md.getInds());
			vao.put(new Vbo(0, 3, md.getVerts()));
			vao.put(new Vbo(1, 2, md.getTextureCoord()));
			vao.put(new Vbo(2, 3, md.getNormals()));
			try {
				vao.setIgnoreCulling(mdl.getBoolean("ignore_culling"));
			} catch (Exception e) {
			}
			models.put(mdl.getString("name"), vao);
		}

		System.out.println("[Engine] Loading textures...");
		Texture ttexture = new Texture(Resource.getTexture("grass.png"));
		JSONArray txtrs = root.getJSONArray("textures");
		for (int i = 0; i < txtrs.length(); i++) {
			JSONObject txtr = txtrs.getJSONObject(i);
			Texture texture = new Texture(Resource.getTexture(txtr.getString("file")));
			textures.put(txtr.getString("name"), texture);
		}
		
		System.out.println("[Engine] Prepairing level...");
		game.onLevelLoaded(level);

		System.out.println("[Engine] Spawning...");
		Body body1 = physics.createTMeshBody(1.0, 10, 0, 0, 0, loader.getVerts(), loader.getInds(), Material.METAL);
		physics.setFixed(body1.getGeom());
		GameObject obj = new GameObject(body1, ttexture, terrainvao);
		scene.add(obj);
		scene.setModels(models);
		scene.setTextures(textures);
		
		System.out.println("[Engine] Spawning entitys...");
		JSONArray array = root.getJSONArray("entitys");
		
		for (int i = 0; i < array.length(); i++) {
			JSONObject entity = array.getJSONObject(i);
			boolean usephys = true;
			try {
				usephys = entity.getBoolean("use_phys");
			} catch (Exception e) {
			}
			Body body2 = null;
			if(usephys){
				body2 = physics.createBoxBody(entity.getDouble("mass"), entity.getDouble("friction"), entity.getDouble("x"), entity.getDouble("y"), entity.getDouble("z"), 2, 2, 2, Material.METAL);
			}
			scene.add(new GameObject(body2, textures.get(entity.getString("texture")), models.get(entity.getString("model")), new Vector3f((float)entity.getDouble("x"), (float)entity.getDouble("y"), (float)entity.getDouble("z"))));
		}
		
		eventsys.sendSignal(Event.END_LOAD_LEVEL);
		System.out.println("[Engine] Done!");
		this.toLoad = false;
	}

	public String getLevel() {
		return level;
	}
}