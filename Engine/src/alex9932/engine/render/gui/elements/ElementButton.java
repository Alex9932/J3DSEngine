package alex9932.engine.render.gui.elements;

import alex9932.engine.main.Main;
import alex9932.engine.render.gui.Font;
import alex9932.utils.IMouseListener;

public class ElementButton extends Element implements IMouseListener{
	private String text;
	private Font font;
	
	public ElementButton(int x, int y, int w, int h, IButtonHandler ibhandler) {
		super(x, y, w, h);
	}
	
	@Override
	public void draw() {
		super.draw();
		if(text != null && font != null) {
			Main.engine.renderer.guirenderer.drawString(font, x + 10, y + 0, text);
		}
	}

	@Override
	public void drawElement() {
		
	}

	public interface IButtonHandler {
		public void invoke();
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
	public void buttonReleased(int button, double x, double y) {
		float gx = this.getGlobalX();
		float gy = this.getGlobalY();

		//System.out.println("Hello!");
		if(x >= gx && x <= gx + width && y >= gy && y <= gy + height) {
			System.out.println("Hello!");
		}
	}

	@Override public void buttonPressed(int button, double x, double y) {}
	@Override public void drag(int button, double x, double y) {}
	@Override public void move(double x, double y) {}
	@Override public void scroll(int scroll) {}
}