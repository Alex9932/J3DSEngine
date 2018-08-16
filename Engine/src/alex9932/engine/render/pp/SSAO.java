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
	private static final int KERNEL_SIZE = 16;
	private static Vector3f[] gKernel = new Vector3f[KERNEL_SIZE];
	private ImageRenderer renderer;
	private int noise;
	
	public SSAO(int w, int h) {
		super(Resource.getShader("pp/pp.vs.h"), Resource.getShader("pp/ssao.ps.h"));
		renderer = new ImageRenderer(w, h);
		Random random = new Random();
		for (int i = 0; i < gKernel.length; i++) {
			gKernel[i] = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
		}
		
		//Noise
		int sizex = 1280;
		int sizey = 720;
		float[] data = new float[sizex * sizey * 3];
		
		for (int i = 0; i < sizex * sizey; i++) {
			data[i * 3] = random.nextFloat();
			data[(i * 3) + 1] = random.nextFloat();
			data[(i * 3) + 2] = 0.0f;
		}
		
		this.noise = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.noise);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB16, sizex, sizey, 0, GL11.GL_RGB, GL11.GL_FLOAT, data);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public void render(Matrix4f proj, GBuffer gbuffer){
		start();
		for (int i = 0; i < gKernel.length; i++) {
			this.loadVector("samples[" + i + "]", gKernel[i]);
		}
		this.loadMatrix4f("projection", proj);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gbuffer.getPositions());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gbuffer.getNormals());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, noise);
		this.loadInt("gPosition", 0);
		this.loadInt("gNormal", 1);
		this.loadInt("texNoise", 2);
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
		this.createUniformLocation("projection");
		this.createUniformLocation("gPosition");
		this.createUniformLocation("gNormal");
		this.createUniformLocation("texNoise");
		
		for (int i = 0; i < KERNEL_SIZE; i++) {
			this.createUniformLocation("samples[" + i + "]");
		}
	}
}