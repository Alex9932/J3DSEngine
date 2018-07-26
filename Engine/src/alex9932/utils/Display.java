package alex9932.utils;

import java.nio.IntBuffer;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class Display {
	private long window;
	private long startTime;
	private int frames, fps;
	private EventSystem eventSystem;
	private long lastFrameTime;
	private float delta;
	private boolean debug = false;
	private int width;
	private int height;
	private String title;
	private boolean fullscreen;

	public Display(int width, int height, String title) {
		this(width, height, title, false);
	}
	
	public Display(int width, int height, String title, boolean fullscreen) {
		this.fullscreen = fullscreen;
		this.width = width;
		this.height = height;
		this.title = title;
		GLFWErrorCallback.createPrint(System.err).set();
		System.out.println("[Display] Init GLFW");
		if (!GLFW.glfwInit()){
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
		System.out.println("[Display] Creating display...");
		createWindow();
		makeContext();
	}

	private void createWindow() {
		window = GLFW.glfwCreateWindow(width, height, title, ((fullscreen) ? GLFW.glfwGetPrimaryMonitor() : MemoryUtil.NULL), MemoryUtil.NULL);
		if (window == MemoryUtil.NULL){
			throw new RuntimeException("Failed to create the GLFW window");
		}

		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			GLFW.glfwGetWindowSize(window, pWidth, pHeight);
			GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
			GLFW.glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
		}
		
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		
		eventSystem = new EventSystem(window);
		GLFW.glfwShowWindow(window);
	}
	
	private void makeContext() {
		GLFW.glfwMakeContextCurrent(window);
		GLFW.glfwSwapInterval(0);
		GL.createCapabilities();
		GLUtil.setupDebugMessageCallback();
	}
	
	public void destroy() {
		Callbacks.glfwFreeCallbacks(window);
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}

	public boolean isCloseRequested() {
		return GLFW.glfwWindowShouldClose(window);
	}

	public void update() {
		GLFW.glfwSwapBuffers(window);
		GLFW.glfwPollEvents();
		eventSystem.update();
		frames++;
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime)/1000f;
		lastFrameTime = currentFrameTime;
		if(System.nanoTime() - startTime > 1000000000){
			fps = frames;
			frames = 0;
			if (debug) {
				System.out.println("Fps: " + fps);
			}
			startTime = System.nanoTime();
		}
	}
	
	private long getCurrentTime() {
		return System.currentTimeMillis();
	}
	
	public void setFullScreen(boolean fullscr) {
		this.fullscreen = fullscr;
		GLFW.glfwSetWindowMonitor(window, ((fullscreen) ? GLFW.glfwGetPrimaryMonitor() : MemoryUtil.NULL), 0, 0, width, height, 1);
		GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		GLFW.glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
	}
	
	public boolean isFullscreen() {
		return this.fullscreen;
	}

	public EventSystem getEventSystem() {
		return eventSystem;
	}
	
	public int getFps() {
		return fps;
	}

	public double getWidth() {
		int[] width = new int[1];
		GLFW.glfwGetWindowSize(window, width, null);
		return width[0];
	}

	public double getHeight() {
		int[] height = new int[1];
		GLFW.glfwGetWindowSize(window, null, height);
		return height[0];
	}

	public float getAspect() {
		return (float)(getWidth() / getHeight());
	}
	
	public float getFrameTime(){
		return delta;
	}

	public void setDebug() {
		this.debug  = true;
	}

	public void setSize(float width, float height) {
		GLFW.glfwSetWindowSize(window, (int)width, (int)height);
	}
	
	public void setIcon(Buffer buffer) {
		GLFW.glfwSetWindowIcon(window, buffer);
	}

	public void setTitle(String title) {
		GLFW.glfwSetWindowTitle(window, title);
	}
}