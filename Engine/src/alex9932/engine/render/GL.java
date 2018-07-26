package alex9932.engine.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import alex9932.utils.Resource;
import alex9932.utils.gl.texture.Texture;

public class GL {
	public static final int GL_FRONT = GL11.GL_FRONT;
	public static final int GL_BACK = GL11.GL_BACK;
	public static final int GL_FRONT_AND_BACK = GL11.GL_FRONT_AND_BACK;

	public static final int GL_POINT = GL11.GL_POINT;
	public static final int GL_LINES = GL11.GL_LINES;
	public static final int GL_LINE_STRIP = GL11.GL_LINE_STRIP;
	public static final int GL_QUADS = GL11.GL_QUADS;
	public static final int GL_QUAD_STRIP = GL11.GL_QUAD_STRIP;
	public static final int GL_TRIANGLES = GL11.GL_TRIANGLES;
	public static final int GL_TRIANGLE_STRIP = GL11.GL_TRIANGLE_STRIP;
	public static final int GL_TRIANGLE_FAN = GL11.GL_TRIANGLE_FAN;

	public GL() {
	}
	
	public static Texture loadTexture(String path) {
		return new Texture(Resource.getTexture(path));
	}
	
	public static void deleteTexture(Texture texture) {
		GL11.glDeleteTextures(texture.getId());
	}
	
	public static void bindTexture(int texture) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
	}
	
	public static void begin(int mode) {
		GL11.glBegin(mode);
	}
	
	public static void end() {
		GL11.glEnd();
	}
	
	public static void color4(float r, float g, float b, float a) {
		GL11.glColor4f(r, g, b, a);
	}
	
	public static void vertex2(float x, float y){
		GL11.glVertex2f(x, y);
	}
	
	public static void texCoord2(float u, float v){
		GL11.glTexCoord2f(u, v);
	}
	
	public static void vertex3(float x, float y, float z){
		GL11.glVertex3f(x, y, z);
	}
	
	public static void texCoord3(float u, float v, float w){
		GL11.glTexCoord3f(u, v, w);
	}
	
	public static void drawImage(int x, int y, int w, int h, Texture texture) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		texture.bind();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(x + w, y);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(x + w, y + h);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(x, y + h);
		GL11.glEnd();
	}
	
	public static void enableAll() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL13.GL_MULTISAMPLE);
	}
	
	public static void disableAll() {
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	public static void disableDepthTest() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	public static void enableDepthTest() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public static void disableCullFace() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public static void enableCullFace() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void cullFace(int gl_side) {
		GL11.glCullFace(gl_side);
	}
}