package alex9932.engine.main;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.opengl.Display;

import alex9932.engine.render.gui.ParsableGui;
import alex9932.engine.render.gui.elements.Element;
import alex9932.engine.render.gui.elements.RootElement;
import alex9932.script.FileIO;
import alex9932.utils.Resource;
import alex9932.utils.gl.Shader;
import alex9932.utils.sound.SoundSystem;
import alex9932.utils.sound.Source;

public class GuiMainMenu extends ParsableGui {
	private static JSONObject config;
	private static Element ROOT_ELEMENT;
	private static List<Source> sounds;
	
	static{
		sounds = new ArrayList<Source>();
		ROOT_ELEMENT = new RootElement((int)Display.getWidth(), (int)Display.getHeight());
		
		try {
			config = new JSONObject(FileIO.read(Resource.getConfig("gui/mainmenu.json")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONArray snds = config.getJSONArray("music");
		for (int i = 0; i < snds.length(); i++) {
			JSONObject snd = snds.getJSONObject(i);
			Source source = SoundSystem.createSource(SoundSystem.getSoundBuffer(Resource.getSound(snd.getString("file"))), 0, 0, 0);
			source.setVolume((float)snd.getDouble("gain"));
			source.repeat(snd.getBoolean("repeat") ? true : false);
			sounds.add(source);
		}
		
		JSONArray elems = config.getJSONArray("ROOT");
		for (int i = 0; i < elems.length(); i++) {
			JSONObject elem = elems.getJSONObject(i);
			ROOT_ELEMENT.add(parseElement(elem));
		}
	}
	
	@Override
	public void render(Shader shader) {
		shader.loadInt("has_texture", 1);
		ROOT_ELEMENT.render();
		ROOT_ELEMENT.draw();
	}

	@Override
	public void show() {
		for (int i = 0; i < sounds.size(); i++) {
			sounds.get(i).play();
		}
	}

	@Override
	public void hide() {
		for (int i = 0; i < sounds.size(); i++) {
			sounds.get(i).stop();
		}
	}
}