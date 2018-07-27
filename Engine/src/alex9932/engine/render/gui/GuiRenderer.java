package alex9932.engine.render.gui;

import org.lwjgl.util.vector.Matrix4f;

import alex9932.utils.MatMath;
import alex9932.utils.Resource;
import alex9932.utils.gl.Shader;

public class GuiRenderer extends Shader{
	private Matrix4f proj;
	private Matrix4f view;
	
	public GuiRenderer() {
		super(Resource.getShader("gui/main.vs.h"), Resource.getShader("gui/main.ps.h"));
		proj = MatMath.createOrtho(0, 1280, 0, 720, -1, 1);
		view = MatMath.createModelMatrix(0, 0, 1, 0, 0, 0, 1);
	}

	@Override
	public void bindAttribs() {
		super.bindAttribute(0, "in_position");
		super.bindAttribute(3, "in_color");
		super.bindAttribute(8, "in_texture_coord");
	}

	@Override
	public void bindUniformLocations() {
		super.createUniformLocation("proj");
		super.createUniformLocation("view");
		super.createUniformLocation("has_texture");
	}
	
	public void render(IGui gui) {
		this.start();
		this.loadInt("has_texture", 1);
		this.loadMatrix4f("proj", proj);
		this.loadMatrix4f("view", view);
		gui.render(this);
		this.stop();
	}
}