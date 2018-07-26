package alex9932.engine.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import alex9932.utils.gl.Vao;
import alex9932.utils.gl.texture.Texture;

public class Scene {
	private ArrayList<GameObject> objects;
	private HashMap<String, Vao> models = new HashMap<String, Vao>();
	private HashMap<String, Texture> textures = new HashMap<String, Texture>();
	
	public Scene() {
		objects = new ArrayList<GameObject>();
	}
	
	public void add(GameObject object) {
		this.objects.add(object);
	}
	
	public void remove(GameObject object) {
		this.objects.remove(object);
	}

	public GameObject getObject(int i) {
		return this.objects.get(i);
	}
	
	public ArrayList<GameObject> getObjects() {
		return this.objects;
	}

	public void destroy() {
		Iterator<String> mkeys = models.keySet().iterator();
		Iterator<String> tkeys = textures.keySet().iterator();
		while (mkeys.hasNext()) {
			String key = mkeys.next();
			models.get(key).destroy();
		}
		while (tkeys.hasNext()) {
			String key = tkeys.next();
			textures.get(key).delete();
		}
	}

	public void setModels(HashMap<String, Vao> models) {
		this.models = models;
	}

	public void setTextures(HashMap<String, Texture> textures) {
		this.textures = textures;
	}
}