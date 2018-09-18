package alex9932.engine.render.gui.elements;

import org.lwjgl.opengl.GL11;

import alex9932.engine.main.Main;
import alex9932.engine.render.gui.Font;
import alex9932.engine.render.gui.IGui;
import alex9932.utils.NVGUtils;

public class ElementText extends Element {
	private String text;
	private float size;
	private Font font;
	
	static {
		NVGUtils.registerFont(IGui.FONT_PATH, IGui.FONT_NAME);
	}
	
	public ElementText(int x, int y, int w, int h) {
		super(x, y, w, h);
	}

	@Override
	public void drawElement() {
		if(text != null && font != null) {
			GL11.glColor3f(0, 0, 1);
			Main.engine.renderer.guirenderer.drawString(font, 10, 0, text);
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
	
	public void setSize(float size) {
		this.size = size;
	}
	
	public String getText() {
		return text;
	}
	
	public float getSize() {
		return size;
	}
}