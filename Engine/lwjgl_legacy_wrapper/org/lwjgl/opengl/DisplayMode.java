package org.lwjgl.opengl;

public class DisplayMode {
	private float w = 1280, h = 720;
	
	public DisplayMode(float width, float height) {
		this.w = width;
		this.h = height;
	}
	
	public float getW() {
		return w;
	}
	
	public float getH() {
		return h;
	}
}