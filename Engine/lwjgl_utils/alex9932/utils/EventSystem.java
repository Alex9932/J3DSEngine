package alex9932.utils;

import java.nio.DoubleBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

public class EventSystem {
	private ArrayList<IKeyListener> keyListeners = new ArrayList<IKeyListener>();
	private ArrayList<IMouseListener> mouseListeners = new ArrayList<IMouseListener>();
	private ArrayList<Integer> keysDown = new ArrayList<Integer>();
	private ArrayList<Integer> buttonsDown = new ArrayList<Integer>();
	private long window;
	
	private double x, y, oldx, oldy;
	private double dx, dy;
	private int mouseState = GLFW.GLFW_RELEASE;
	private int button;
	private boolean isGrabbed;
	
	public EventSystem(long window) {
		this.window = window;
		setWindowCallbacks();
	}
	
	public void setWindowCallbacks() {
		GLFW.glfwSetKeyCallback(window, new GLFWKeyCallbackI() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if(action == GLFW.GLFW_PRESS){
					keysDown.add((Integer)key);
				}else if(action == GLFW.GLFW_RELEASE){
					keysDown.remove((Integer)key);
				}
				for (int i = 0; i < keyListeners.size(); i++) {
					if(action == GLFW.GLFW_PRESS){
						keyListeners.get(i).keyPressed(key);
					}else if(action == GLFW.GLFW_RELEASE){
						keyListeners.get(i).keyReleased(key);
					}
				}
			}
		});
		
		GLFW.glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallbackI() {
			@Override
			public void invoke(long window, int key, int action, int hz) {
				if(action == GLFW.GLFW_PRESS){
					buttonsDown.add((Integer)key);
				}else if(action == GLFW.GLFW_RELEASE){
					buttonsDown.remove((Integer)key);
				}
				
				double[] xpos = new double[1];
				double[] ypos = new double[1];
				GLFW.glfwGetCursorPos(window, xpos, ypos);
				double x = xpos[0];
				double y = ypos[0];
				mouseState = action;
				button = key;
				
				for (int i = 0; i < mouseListeners.size(); i++) {
					if(action == GLFW.GLFW_PRESS){
						mouseListeners.get(i).buttonPressed(key, x, y);
					}else if(action == GLFW.GLFW_RELEASE){
						mouseListeners.get(i).buttonReleased(key, x, y);
					}
				}
			}
		});
	}
	
	public void setWindow(long window) {
		this.window = window;
		setWindowCallbacks();
	}
	
	public void update() {
		dx = dy = 0;
		
		double[] xpos = new double[1];
		double[] ypos = new double[1];
		GLFW.glfwGetCursorPos(window, xpos, ypos);
		this.x = xpos[0];
		this.y = ypos[0];

		/**int[] width = new int[1];
		int[] height = new int[1];
		GLFW.glfwGetWindowSize(window, width, height);
		int w = width[0];
		int h = height[0];
		
		if(x < 0 || y < 0 || x > w || y > h){
			return;
		}**/
		
		if(isGrabbed){
			DoubleBuffer xp = BufferUtils.createDoubleBuffer(1);
			DoubleBuffer yp = BufferUtils.createDoubleBuffer(1);
			GLFW.glfwGetCursorPos(window, xp, yp);
			xp.rewind();
			yp.rewind();
			dx = 640 - xp.get();
			dy = 400 - yp.get();
			GLFW.glfwSetCursorPos(window, 640, 400);
		}else{
			if(this.x == this.oldx && this.y == this.oldy){}else{ // if(this.x != this.oldx && this.y != this.oldy)
				for (int i = 0; i < mouseListeners.size(); i++) {
					if(mouseState == GLFW.GLFW_PRESS){
						mouseListeners.get(i).drag(button, x, y);
					}else{
						mouseListeners.get(i).move(x, y);
					}
				}
			}
			
			if (oldx > 0 && oldy > 0) {
				double deltax = x - oldx;
				double deltay = y - oldy;
				boolean rotateX = deltax != 0;
				boolean rotateY = deltay != 0;
				if (rotateX) {
					dy = (float) deltax;
				}
				if (rotateY) {
					dx = (float) deltay;
				}
			}
		}
		
		this.oldx = this.x;
		this.oldy = this.y;
	}

	public void addKeyListener(IKeyListener listener) {
		keyListeners.add(listener);
	}
	
	public void removeKeyListener(IKeyListener listener) {
		keyListeners.remove(listener);
	}
	
	public void addMouseListener(IMouseListener listener) {
		mouseListeners.add(listener);
	}
	
	public void removeMouseListener(IMouseListener listener) {
		mouseListeners.remove(listener);
	}

	public float getMouseDX() {
		return (float)dx;
	}

	public float getMouseDY() {
		return (float)dy;
	}
	
	public int getWidth() {
		int[] w = new int[1];
		GLFW.glfwGetWindowSize(window, w, null);
		return w[0];
	}
	
	public int getHeight() {
		int[] h = new int[1];
		GLFW.glfwGetWindowSize(window, null, h);
		return h[0];
	}

	public boolean isKeyDown(int key) {
		for (int i = 0; i < keysDown.size(); i++) {
			if(keysDown.get(i) == key){
				return true;
			}
		}
		return false;
	}

	public void setGrabbed(boolean grab) {
		if(grab){
			int[] xpos = new int[1], ypos = new int[1];
			GLFW.glfwGetWindowPos(window, xpos, ypos);
			GLFW.glfwSetCursorPos(window, xpos[0] + (getWidth() / 2), ypos[0] + (getHeight() / 2));
			GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		}else{
			GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		}
		this.isGrabbed = grab;
	}

	public double getMouseX() {
		return x;
	}

	public double getMouseY() {
		return y;
	}

	public boolean isButtonDown(int button) {
		for (int i = 0; i < buttonsDown.size(); i++) {
			if(buttonsDown.get(i) == button){
				return true;
			}
		}
		return false;
	}
}