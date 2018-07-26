package alex9932.engine.render.pp;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import alex9932.engine.render.ImageRenderer;
import alex9932.utils.Resource;
import alex9932.utils.gl.Shader;

public class VerticalBlur extends Shader{
	
	private ImageRenderer renderer;
	
	public VerticalBlur(int targetFboWidth, int targetFboHeight){
		super(Resource.getShader("pp/vblur.vs.h"), Resource.getShader("pp/blur.ps.h"));
		renderer = new ImageRenderer(targetFboWidth, targetFboHeight);
		start();
		loadTargetHeight(targetFboHeight);
		stop();
	}

	
	public void render(int texture){
		start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		renderer.renderQuad();
		stop();
	}
	
	public int getOutputTexture(){
		return renderer.getOutputTexture();
	}
	
	@Override
	public void destroy() {
		renderer.cleanUp();
		super.destroy();
	}
	
	public void loadTargetHeight(float height){
		super.loadFloat("targetHeight", height);
	}

	@Override
	public void bindUniformLocations() {	
		super.createUniformLocation("targetHeight");
	}

	@Override
	public void bindAttribs() {
		super.bindAttribute(0, "position");
	}
}
