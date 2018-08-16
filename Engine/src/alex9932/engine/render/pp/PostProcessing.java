package alex9932.engine.render.pp;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import alex9932.engine.render.GBuffer;
import alex9932.utils.gl.Vao;
import alex9932.utils.gl.Vbo;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static Vao quad;

	private static Contrast contrast = new Contrast();
	private static VerticalBlur vblur = new VerticalBlur(1280, 720);
	private static HorizontalBlur hblur = new HorizontalBlur(1280, 720);
	private static Combiner combiner = new Combiner((int)Display.getWidth(), (int)Display.getHeight());
	private static Bright bright = new Bright((int)Display.getWidth(), (int)Display.getHeight());
	private static SSAO ssao = new SSAO((int)Display.getWidth(), (int)Display.getHeight());
	private static Lighting lighting = new Lighting((int)Display.getWidth(), (int)Display.getHeight());

	public static void init(){
		quad = new Vao(null);
		quad.put(new Vbo(0, 2, POSITIONS));
	}
	
	public static void doPostProcessing(Vector3f lightDirection, Matrix4f proj, GBuffer gbuffer){
		start();
		ssao.render(proj, gbuffer);
		
		//Lighting
		lighting.render(gbuffer, ssao.getOutputTexture(), lightDirection);
		
		bright.render(lighting.getOutputTexture());
		vblur.render(bright.getOutputTexture());
		hblur.render(vblur.getOutputTexture());
		combiner.render(hblur.getOutputTexture(), lighting.getOutputTexture());
		//contrast.render(ssao.getOutputTexture());
		contrast.render(combiner.getOutputTexture());
		//contrast.render(hblur.getOutputTexture());
		end();
	}
	
	public static void cleanUp(){

	}
	
	private static void start(){
		quad.bind();
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void end(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}


}
