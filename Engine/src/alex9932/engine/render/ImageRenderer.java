package alex9932.engine.render;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import alex9932.utils.gl.Fbo;

public class ImageRenderer {
	private Fbo fbo;

	public ImageRenderer(int width, int height) {
		this.fbo = new Fbo(Display.getDisplay(), width, height);
	}

	public ImageRenderer() {}

	public void renderQuad() {
		if (fbo != null) {
			fbo.bind();
		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		if (fbo != null) {
			fbo.unbind();
		}
	}

	public int getOutputTexture() {
		return fbo.getRenderTexture();
	}

	public void cleanUp() {
		if (fbo != null) {
			fbo.destroy();
		}
	}
}