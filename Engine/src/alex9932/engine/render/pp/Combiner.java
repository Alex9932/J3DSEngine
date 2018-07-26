package alex9932.engine.render.pp;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import alex9932.engine.render.ImageRenderer;
import alex9932.utils.Resource;
import alex9932.utils.gl.Shader;

public class Combiner extends Shader{
	private ImageRenderer renderer;
	
	public Combiner(int width, int height) {
		super(Resource.getShader("pp/pp.vs.h"), Resource.getShader("pp/combiner.ps.h"));
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
		this.createUniformLocation("texture1");
	}
	
	public void render(int texture, int texture2){
		start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture2);
		loadInt("texture0", 0);
		loadInt("texture1", 1);
		renderer.renderQuad();
		stop();
	}
}