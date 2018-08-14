package alex9932.utils.gl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import alex9932.utils.Display;

public class Fbo {
	public static final int FBO_DEFAULT = 0x00;      //Default fbo
	public static final int FBO_MULTISAMPLED = 0x01; //Multisampled fbo
	public static final int FBO_MRT = 0x02;          //Multiple render targets fbo
	
	private int fbo;
	private int renderedTexture;
	private int depthTexture;
	private int[] renderedBuffer;
	private int depthBuffer;
	private int width;
	private int height;
	private int type;
	private int samples;
	private Display display;
	private int[] buffer;

	public Fbo(Display display, int width, int height) {
		this(display, width, height, FBO_DEFAULT, 1, 1);
	}

	public Fbo(Display display, int width, int height, int type) {
		this(display, width, height, type, 1, 1);
	}
	
	public Fbo(Display display, int width, int height, int type, int samples) {
		this(display, width, height, type, samples, 1);
	}

	public Fbo(Display display, int width, int height, int type, int samples, int size) {
		this.display = display;
		this.width = width;
		this.height = height;
		this.type = type;
		this.samples = samples;
		this.renderedBuffer = new int[size];
		createBuffer();
		checkError();
	}

	private void checkError() {
		String message = GL11.glGetString(GL11.glGetError());
		if(message != null) {
			System.out.println(message);
		}
		int Status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		if (Status != GL30.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("FB error, status: 0xA" + Status);
		}
	}

	public void resolveToFbo(int attachment, Fbo out) {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, out.fbo);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.fbo);
		GL11.glReadBuffer(attachment);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, out.width, out.height, GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		this.unbind();
	}
	
	public void resolveToScreen() {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.fbo);
		GL11.glDrawBuffer(GL11.GL_BACK);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, (int)display.getWidth(), (int)display.getHeight(), GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
		this.unbind();
	}

	private void createBuffer() {
		this.fbo = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.fbo);
		if(this.type == FBO_MULTISAMPLED){
			GL11.glEnable(GL13.GL_MULTISAMPLE);
			this.renderedBuffer[0] = createColorAttachment(GL30.GL_COLOR_ATTACHMENT0);
		} else if(this.type == FBO_DEFAULT) {
			this.renderedTexture = createColorTexture();
		} else if(this.type == FBO_MRT) {
			GL11.glEnable(GL13.GL_MULTISAMPLE);
			for (int i = 0; i < renderedBuffer.length; i++) {
				renderedBuffer[i] = createColorAttachment(GL30.GL_COLOR_ATTACHMENT0 + i);
			}
			
			
			buffer = new int[renderedBuffer.length];
			for (int i = 0; i < buffer.length; i++) {
				buffer[i] = GL30.GL_COLOR_ATTACHMENT0 + i;
			}
		} else {
			System.out.println("Unsupported FBO type.");
		}
		
		if(this.type == FBO_MULTISAMPLED || this.type == FBO_MRT){
			depthBuffer = GL30.glGenRenderbuffers();
			GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
			GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samples, GL14.GL_DEPTH_COMPONENT24, width, height);
			GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
			
		} else if(this.type == FBO_DEFAULT) {
			this.depthTexture = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.depthTexture);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, this.width, this.height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (FloatBuffer) null);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, this.depthTexture, 0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		}
	}
	
	private int createColorTexture() {
		int renderTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, renderTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, this.width, this.height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, renderTexture, 0);
		return renderTexture;
	}

	private int createColorAttachment(int attachment) {
		int renderedBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderedBuffer);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samples, GL11.GL_RGBA8, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, attachment, GL30.GL_RENDERBUFFER, renderedBuffer);
		return renderedBuffer;
	}

	public void bind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.fbo);
		GL11.glViewport(0, 0, this.width, this.height);
		if(this.type == FBO_MRT && buffer != null){
			GL20.glDrawBuffers(buffer);
		}
	}
	
	public void unbind() {
		GL20.glDrawBuffers(new int[]{GL30.GL_COLOR_ATTACHMENT0});
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, (int)display.getWidth(), (int)display.getHeight());
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getRenderTexture() {
		return renderedTexture;
	}
	
	public int getDepthTexture() {
		return depthTexture;
	}

	public void destroy() {
		GL11.glDeleteTextures(this.renderedTexture);
		GL11.glDeleteTextures(this.depthTexture);
		GL30.glDeleteFramebuffers(this.fbo);
	}
	
	public String getSize() {
		return this.width + "x" + this.height;
	}

	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		this.destroy();
		this.createBuffer();
	}
}