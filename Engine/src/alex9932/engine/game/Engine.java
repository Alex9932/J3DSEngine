package alex9932.engine.game;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
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
import alex9932.utils.MatMath;
import alex9932.utils.NVGUtils;
import alex9932.utils.Profiler;
import alex9932.utils.Resource;
import alex9932.utils.Timer;
import alex9932.utils.gl.Vao;
import alex9932.utils.gl.Vbo;
import alex9932.utils.gl.texture.Texture;
import alex9932.utils.sound.SoundSystem;

public class Engine {
	public static final String _version = "0.3a pre 7";
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
	private boolean level_staticonly;
	
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
		System.out.println("[Engine] [OpenGL] Renderer: " + GL11.glGetString(GL11.GL_RENDERER));
		System.out.println("[Engine] [OpenGL] GLSL: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
		eventsys = new EventSystem();
		script = new ScriptsEngine(this);
		eventsys.sendSignal(Event.STARTUP);
		SoundSystem.init();
		renderer = new Renderer();
		physics = new Physics();
	}
	
	public void run() throws Exception {
		NVGUtils.registerFont(Resource.getTexture("fonts/gui.ttf"), "font");
		
		game.startup();
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
		loadLevel(level, false);
	}

	public void loadLevel(String level, boolean b) {
		this.level = level;
		this.toLoad = true;
		this.level_staticonly = b;
	}

	private void _loadLevel(String level) throws Exception {
		_loadLevel(level, level_staticonly);
	}
	
	private void _loadLevel(String level, boolean staticonly) throws Exception {
		Display.getDisplay().getEventSystem().setGrabbed(true);
		System.out.println("[Engine] Starting load level: " + level + "...");
		eventsys.sendSignal(Event.START_LOAD_LEVEL);
		eventsys.sendSignal(Event.ON_LOAD_EVENT);
		game.onStartLevelLoading(level);
		scene.destroy();
		physics.destroy();
		physics = new Physics();
		scene = null;
		scene = new Scene();
		JSONObject sroot = new JSONObject(FileIO.read(Resource.getLevel(level + "/sspawn.json")));
		JSONObject droot = null;
		if(!staticonly) {
			droot = new JSONObject(FileIO.read(Resource.getLevel(level + "/dspawn.json")));
		}
		HashMap<String, Texture> textures = new HashMap<String, Texture>();
		HashMap<String, Vao> models = new HashMap<String, Vao>();
		
		System.out.println("[Engine] Loading geometry...");
		FmlLoader loader = new FmlLoader(Resource.getLevel(level + "/geometry.fml"));
		Vao terrainvao = new Vao(Resource.getLevel(level + "/geometry.fml"));
		terrainvao.setIndices(loader.getInds());
		terrainvao.put(new Vbo(0, 3, loader.getVerts()));
		terrainvao.put(new Vbo(1, 2, loader.getTextureCoord()));
		terrainvao.put(new Vbo(2, 3, loader.getNormals()));

		JSONArray mdls = sroot.getJSONArray("models");
		for (int i = 0; i < mdls.length(); i++) {
			JSONObject mdl = mdls.getJSONObject(i);
			FmlLoader md = new FmlLoader(Resource.getModel(mdl.getString("file")));
			Vao vao = new Vao(Resource.getModel(mdl.getString("file")));
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
		if(!staticonly) {
			mdls = droot.getJSONArray("models");
			for (int i = 0; i < mdls.length(); i++) {
				JSONObject mdl = mdls.getJSONObject(i);
				FmlLoader md = new FmlLoader(Resource.getModel(mdl.getString("file")));
				Vao vao = new Vao(Resource.getModel(mdl.getString("file")));
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
		}

		System.out.println("[Engine] Loading textures...");
		Texture ttexture = new Texture(Resource.getTexture("grass.png"));
		JSONArray txtrs = sroot.getJSONArray("textures");
		for (int i = 0; i < txtrs.length(); i++) {
			JSONObject txtr = txtrs.getJSONObject(i);
			Texture texture = new Texture(Resource.getTexture(txtr.getString("file")));
			textures.put(txtr.getString("name"), texture);
		}
		if(!staticonly) {
			txtrs = droot.getJSONArray("textures");
			for (int i = 0; i < txtrs.length(); i++) {
				JSONObject txtr = txtrs.getJSONObject(i);
				Texture texture = new Texture(Resource.getTexture(txtr.getString("file")));
				textures.put(txtr.getString("name"), texture);
			}
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
		JSONArray array = sroot.getJSONArray("entitys");
		
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
		

		if(!staticonly) {
			array = droot.getJSONArray("entitys");
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
				GameObject gobj = new GameObject(body2, textures.get(entity.getString("texture")), models.get(entity.getString("model")), new Vector3f((float)entity.getDouble("x"), (float)entity.getDouble("y"), (float)entity.getDouble("z")));
				gobj.setStatic(false);
				scene.add(gobj);
			}
		}
		
		eventsys.sendSignal(Event.END_LOAD_LEVEL);
		System.out.println("[Engine] Done!");
		this.toLoad = false;
	}
	
	public void load(String savename) throws Exception {
		JSONObject root = new JSONObject(FileIO.read(Resource.getSave(savename) + savename + ".sav"));
		JSONObject level = root.getJSONObject("level");
		_loadLevel(level.getString("name"), true);

		HashMap<String, Texture> tmap = new HashMap<String, Texture>();
		HashMap<String, Vao> vmap = new HashMap<String, Vao>();
		
		JSONArray entitys = level.getJSONArray("entitys");
		for (int i = 0; i < entitys.length(); i++) {
			JSONObject obj = entitys.getJSONObject(i);
			String tex = obj.getString("texture");
			String mdl = obj.getString("model");
			if(tmap.get(tex) == null) {
				tmap.put(tex, new Texture(tex));
			}
			if(vmap.get(mdl) == null) {
				Vao vao = new Vao(mdl);
				FmlLoader fml = new FmlLoader(mdl);
				vao.setIndices(fml.getInds());
				vao.put(new Vbo(0, 3, fml.getVerts()));
				vao.put(new Vbo(1, 2, fml.getTextureCoord()));
				vao.put(new Vbo(2, 3, fml.getNormals()));
				vmap.put(mdl, vao);
			}
		}
		
		System.out.println("[Engine] Spawning...");
		for (int i = 0; i < entitys.length(); i++) {
			JSONObject entity = entitys.getJSONObject(i);
			Body body = physics.createBoxBody(15, 13, entity.getDouble("x"), entity.getDouble("y"), entity.getDouble("z"), 2, 2, 2, Material.METAL);
			GameObject obj = new GameObject(body, tmap.get(entity.getString("texture")), vmap.get(entity.getString("model")));
			obj.setStatic(false);
			body.getGeom().setRotation(MatMath.createDMat3d(entity.getJSONArray("matrix")));
			scene.add(obj);
		}
		
		JSONObject player = root.getJSONObject("player");
		renderer.getCamera().setPositionAXAY(player.getDouble("x"), player.getDouble("y"), player.getDouble("z"), player.getDouble("ax"), player.getDouble("ay"));
	}
	
	public void save(String savename) throws IOException {
		new File("./saves/" + savename).mkdirs();
		
		double player_x = renderer.getCamera().getX();
		double player_y = renderer.getCamera().getY();
		double player_z = renderer.getCamera().getZ();
		double player_ax = renderer.getCamera().getAngleX();
		double player_ay = renderer.getCamera().getAngleY();
		
		JSONObject root = new JSONObject();
		JSONObject player = new JSONObject();
		player.put("x", player_x);
		player.put("y", player_y);
		player.put("z", player_z);
		player.put("ax", player_ax);
		player.put("ay", player_ay);

		JSONObject level = new JSONObject();
		level.put("name", this.level);
		Collection<JSONObject> o = serializeEntitys(scene);
		JSONArray entitys = new JSONArray(o);
		level.put("entitys", entitys);

		root.put("player", player);
		root.put("level", level);
		
		FileWriter writer = new FileWriter(new File("./saves/" + savename + "/" + savename + ".sav"));
		System.out.println(root.toString());
		writer.write(root.toString());
		writer.close();
	}

	private ArrayList<JSONObject> serializeEntitys(Scene scene) {
		ArrayList<JSONObject> objs = new ArrayList<JSONObject>();
		ArrayList<GameObject> gameobjects = scene.getObjects();
		for (int i = 0; i < gameobjects.size(); i++) {
			GameObject obj = gameobjects.get(i);
			if(!obj.isStatic()) {
				JSONObject gobj = new JSONObject();
				gobj.put("x", obj.getX());
				gobj.put("y", obj.getY());
				gobj.put("z", obj.getZ());
				ArrayList<Float> mat = new ArrayList<Float>();
				FloatBuffer matrix = obj.getModelMatrix().getGLMatrix();
				for (int j = 0; j < 16; j++) {
					mat.add(matrix.get(j));
				}
				gobj.put("matrix", mat);
				gobj.put("texture", obj.getTexture().getPath());
				if(obj.getSpecular() != null) {
					gobj.put("specular", obj.getSpecular().getPath());
				}
				gobj.put("model", obj.getVao().getPath());

				/**JSONObject phys = new JSONObject();
				phys.put("co_type", obj.getBody().getType());
				phys.put("mass", obj.getBody().getBody().getMass().getMass());
				switch (obj.getBody().getType()) {
				case DGeom.dBoxClass:
					//phys.put("", "");
					break;
				case DGeom.dSphereClass:
					//phys.put("radius", obj.getBody().getGeom().);
					break;
				case DGeom.dTriMeshClass:
					
					break;
				default:
					break;
				}
				gobj.put("physics", phys);**/
				objs.add(gobj);
			}
		}
		return objs;
	}

	public String getLevel() {
		return level;
	}

	public int getFps() {
		return Display.getFps();
	}
}