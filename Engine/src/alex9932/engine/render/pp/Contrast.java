package alex9932.engine.render.pp;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import alex9932.engine.render.ImageRenderer;
import alex9932.utils.Resource;
import alex9932.utils.gl.Shader;

public class Contrast extends Shader{
	private ImageRenderer image;

	public Contrast() {
		super(Resource.getShader("pp/pp.vs.h"), Resource.getShader("pp/contrast.ps.h"));
		this.image = new ImageRenderer();
	}
	
	public void render(int texture) {
		start();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		image.renderQuad();
		
		stop();
	}

	@Override
	public void bindUniformLocations() {	
	}

	@Override
	public void bindAttribs() {
		super.bindAttribute(0, "position");
	}

	@Override
	public void destroy() {
		this.image.cleanUp();
		super.destroy();
	}
}