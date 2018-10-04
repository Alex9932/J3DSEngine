package alex9932.engine.render.pp;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import alex9932.engine.game.Display;
import alex9932.engine.render.GBuffer;
import alex9932.utils.gl.Vao;
import alex9932.utils.gl.Vbo;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static Vao quad;

	private static Contrast contrast;
	private static VerticalBlur vblur;
	private static HorizontalBlur hblur;
	private static Combiner combiner;
	private static Bright bright;
	private static SSAO ssao;
	private static Lighting lighting;

	public static void init(){
		quad = new Vao(null);
		quad.put(new Vbo(0, 2, POSITIONS));

		contrast = new Contrast();
		vblur = new VerticalBlur(Display.getWidth(), Display.getHeight());
		hblur = new HorizontalBlur(Display.getWidth(), Display.getHeight());
		combiner = new Combiner(Display.getWidth(), Display.getHeight());
		bright = new Bright(Display.getWidth(), Display.getHeight());
		ssao = new SSAO(Display.getWidth(), Display.getHeight());
		lighting = new Lighting(Display.getWidth(), Display.getHeight());
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
	
	public static void destroy(){
		contrast.destroy();
		vblur.destroy();
		hblur.destroy();
		combiner.destroy();
		bright.destroy();
		lighting.destroy();
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
