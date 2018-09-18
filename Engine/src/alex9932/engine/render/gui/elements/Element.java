package alex9932.engine.render.gui.elements;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public abstract class Element {
	private List<Element> childs;
	private Element parent;
	
	protected int x, y;
	protected int width, height;
	protected int fbo;
	protected int fboTexture, fbodepth;
	
	public Element(int x, int y, int w, int h) {
		this.childs = new ArrayList<Element>();
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		
		this.fbo = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.fbo);
		
		this.fboTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.fboTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, this.width, this.height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, this.fboTexture, 0);
		
		this.fbodepth = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.fbodepth);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, this.width, this.height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (FloatBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, this.fbodepth, 0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	public void add(Element element) {
		element.setParent(this);
		childs.add(element);
	}
	
	public void remove(Element element) {
		childs.remove(element);
	}
	
	public void render() {
		for (int i = 0; i < childs.size(); i++) {
			childs.get(i).render();
		}
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.fbo);
		
		drawElement();
		
		for (int i = 0; i < childs.size(); i++) {
			childs.get(i).draw();
		}
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	public abstract void drawElement();

	public Element getParent() {
		return parent;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getFboTexture() {
		return fboTexture;
	}
	
	public void setParent(Element parent) {
		this.parent = parent;
	}

	public void draw() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fboTexture);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(x + width, y);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(x + width, y + height);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(x, y + height);
		GL11.glEnd();
	}
}