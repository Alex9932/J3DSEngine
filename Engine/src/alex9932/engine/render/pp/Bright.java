package alex9932.engine.render.pp;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import alex9932.engine.render.ImageRenderer;
import alex9932.utils.Resource;
import alex9932.utils.gl.Shader;

public class Bright extends Shader{
	private ImageRenderer renderer;
	
	public Bright(int width, int height) {
		super(Resource.getShader("pp/pp.vs.h"), Resource.getShader("pp/bright.ps.h"));
		renderer = new ImageRenderer(width, height);
	}
	
	public int getOutputTexture(){
		return renderer.getOutputTexture();
	}

	@Override
	public void bindAttribs() {
		super.bindAttribute(0, "position");
	}

	@Override
	public void bindUniformLocations() {
		this.createUniformLocation("texture0");
	}
	
	public void render(int texture){
		start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		loadInt("texture0", 0);
		renderer.renderQuad();
		stop();
	}
}