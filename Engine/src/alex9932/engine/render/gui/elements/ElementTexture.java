package alex9932.engine.render.gui.elements;

import org.lwjgl.opengl.GL11;

import alex9932.utils.gl.texture.Texture;

public class ElementTexture extends Element{
	private Texture texture;
	
	public ElementTexture(int x, int y, int w, int h) {
		super(x, y, w, h);
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	@Override
	public void drawElement() {
		if(texture != null){
			texture.bind();
		} else {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		}
		GL11.glColor3f(1, 1, 1);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(0, 0);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(width, 0);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(width, height);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(0, height);
		GL11.glEnd();
	}
}