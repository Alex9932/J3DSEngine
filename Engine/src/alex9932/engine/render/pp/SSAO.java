package alex9932.engine.render.pp;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import alex9932.engine.render.GBuffer;
import alex9932.engine.render.ImageRenderer;
import alex9932.utils.Resource;
import alex9932.utils.gl.Shader;

public class SSAO extends Shader {
	private static final int MAX_KERNEL_SIZE = 128;
	private static Vector3f[] gKernel = new Vector3f[MAX_KERNEL_SIZE];
	private ImageRenderer renderer;
	
	public SSAO(int w, int h) {
		super(Resource.getShader("pp/pp.vs.h"), Resource.getShader("pp/ssao.ps.h"));
		renderer = new ImageRenderer(w, h);
		Random random = new Random();
		for (int i = 0; i < gKernel.length; i++) {
			gKernel[i] = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
		}
	}
	
	public void render(Matrix4f proj, GBuffer gbuffer){
		start();
		for (int i = 0; i < gKernel.length; i++) {
			this.loadVector("gKernel[" + i + "]", gKernel[i]);
		}
		this.loadMatrix4f("gProj", proj);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gbuffer.getPositions());
		renderer.renderQuad();
		stop();
	}
	
	public int getOutputTexture(){
		return renderer.getOutputTexture();
	}

	@Override public void bindAttribs() {
		super.bindAttribute(0, "position");
	}
	
	@Override public void bindUniformLocations() {
		this.createUniformLocation("gProj");
		this.createUniformLocation("gSampleRad");
		for (int i = 0; i < MAX_KERNEL_SIZE; i++) {
			this.createUniformLocation("gKernel[" + i + "]");
		}
	}
}