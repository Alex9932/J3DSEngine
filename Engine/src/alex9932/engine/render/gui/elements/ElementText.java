package alex9932.engine.render.gui.elements;

import alex9932.engine.main.Main;
import alex9932.engine.render.gui.Font;
import alex9932.engine.render.gui.IGui;
import alex9932.utils.NVGUtils;

public class ElementText extends Element {
	private String text;
	private Font font;
	
	static {
		NVGUtils.registerFont(IGui.FONT_PATH, IGui.FONT_NAME);
	}
	
	public ElementText(int x, int y, int w, int h) {
		super(x, y, w, h);
	}

	@Override
	public void draw() {
		if(text != null && font != null) {
			Main.engine.renderer.guirenderer.drawString(font, x + 10, y + 0, text);
		}
	}
	
	
	public void setFont(Font font) {
		this.font = font;
	}
	
	public Font getFont() {
		return font;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	@Override
	public void drawElement() {
		
	}
}