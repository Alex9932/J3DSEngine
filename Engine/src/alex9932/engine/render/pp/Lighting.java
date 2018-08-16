package alex9932.engine.render.pp;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector3f;

import alex9932.engine.render.GBuffer;
import alex9932.engine.render.ImageRenderer;
import alex9932.utils.Resource;
import alex9932.utils.gl.Shader;

public class Lighting extends Shader{
	private ImageRenderer renderer;
	
	public Lighting(int width, int height) {
		super(Resource.getShader("pp/pp.vs.h"), Resource.getShader("pp/lighting.ps.h"));
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
		this.createUniformLocation("diffuse");
		this.createUniformLocation("normals");
		this.createUniformLocation("specular");
		this.createUniformLocation("ssao");
		this.createUniformLocation("lightDirection");
	}

	public void render(GBuffer gbuffer, int ssao, Vector3f lightDirection) {
		start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gbuffer.getDiffuse());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gbuffer.getNormals());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gbuffer.getSpecular());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, ssao);
		loadInt("diffuse", 0);
		loadInt("normals", 1);
		loadInt("specular", 2);
		loadInt("ssao", 3);
		loadVector("lightDirection", lightDirection);
		renderer.renderQuad();
		stop();
	}
}