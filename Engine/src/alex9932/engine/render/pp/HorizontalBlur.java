package alex9932.engine.render.pp;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import alex9932.engine.render.ImageRenderer;
import alex9932.utils.Resource;
import alex9932.utils.gl.Shader;

public class HorizontalBlur extends Shader{
	
	private ImageRenderer renderer;
	
	public HorizontalBlur(int targetFboWidth, int targetFboHeight){
		super(Resource.getShader("pp/hblur.vs.h"), Resource.getShader("pp/blur.ps.h"));
		start();
		loadTargetWidth(targetFboWidth);
		stop();
		renderer = new ImageRenderer(targetFboWidth, targetFboHeight);
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
	
	public void loadTargetWidth(float width){
		super.loadFloat("targetWidth", width);
	}
	
	@Override
	public void bindUniformLocations() {
		super.createUniformLocation("targetWidth");
	}

	@Override
	public void bindAttribs() {
		super.bindAttribute(0, "position");
	}
}
