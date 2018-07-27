package alex9932.engine.main;

import org.lwjgl.opengl.GL11;

import alex9932.engine.render.gui.IGui;
import alex9932.utils.Resource;
import alex9932.utils.gl.Shader;
import alex9932.utils.gl.texture.Texture;

public class Gui implements IGui {
	private static final Texture texture;
	
	static {
		texture = new Texture(Resource.getTexture("default.png"));
	}
	
	@Override
	public void render(Shader shader) {
		shader.loadInt("has_texture", 1);
		texture.connectToAndBind(0);
		GL11.glColor3f(1, 1, 1);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2d(100, 100);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2d(1180, 100);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2d(180, 620);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2d(100, 620);
		GL11.glEnd();
		GL11.glColor3f(1, 1, 1);
		shader.loadInt("has_texture", 0);
	}
}