package alex9932.engine.render.gui;

import alex9932.utils.gl.Shader;

public interface IGui {
	String FONT_PATH = "";
	String FONT_NAME = "gui_font";
	public void render(Shader shader);
	public void show();
	public void hide();
}