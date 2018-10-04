package alex9932.engine.render.gui;

import org.json.JSONArray;
import org.json.JSONObject;

import alex9932.engine.game.Display;
import alex9932.engine.render.gui.elements.Element;
import alex9932.engine.render.gui.elements.ElementButton;
import alex9932.engine.render.gui.elements.ElementText;
import alex9932.engine.render.gui.elements.ElementTexture;
import alex9932.utils.Resource;
import alex9932.utils.gl.texture.Texture;

public abstract class ParsableGui implements IGui{
	public static Element parseElement(JSONObject elem) {
		int x = elem.getInt("x");
		int y = elem.getInt("y");
		int w = 0, h = 0;

		try {
			String keyw = elem.getString("w");
			if(keyw.equals("DYN_DISP_WIDTH")){w = Display.getWidth();} else if(keyw.equals("DYN_DISP_HEIGHT")){w = Display.getHeight();}
		} catch (Exception e) {
			w = elem.getInt("w");
		}
		
		try {
			String keyh = elem.getString("h");
			if(keyh.equals("DYN_DISP_WIDTH")){h = Display.getWidth();} else if(keyh.equals("DYN_DISP_HEIGHT")){h = Display.getHeight();}
		} catch (Exception e) {
			h = elem.getInt("h");
		}
		
		String type = elem.getString("type");
		
		Element element = null;
		if (type.equals("texture")) {
			element = new ElementTexture(x, y, w, h);
			((ElementTexture)element).setTexture(new Texture(Resource.getTexture(elem.getString("file"))));
		} else if (type.equals("text")) {
			element = new ElementText(x, y, w, h);
			((ElementText)element).setText(elem.getString("text"));
			((ElementText)element).setFont(new Font(Resource.getTexture("fonts/gui.ttf"), (float)elem.getDouble("size")));
		}
		
		JSONArray childs = elem.getJSONArray("childs");
		for (int i = 0; i < childs.length(); i++) {
			element.add(parseElement(childs.getJSONObject(i)));
		}
		
		ElementButton button = new ElementButton(100, 100, 200, 50, new ElementButton.IButtonHandler() {
			@Override
			public void invoke() {
				
			}
		});
		button.setText("Zhopa");
		button.setFont(new Font(Resource.getTexture("fonts/gui.ttf"), 20));
		Display.getEventSystem().addMouseListener(button);
		
		element.add(button);
		
		return element;
	}
}