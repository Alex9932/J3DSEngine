package org.lwjgl.opengl;

public class Display {
	private static alex9932.utils.Display display;
	
	public static void create() {
		display = new alex9932.utils.Display(1280, 720, "Game");
	}
	
	public static void setDisplayMode(DisplayMode mode) {
		display.setSize(mode.getW(), mode.getH());
	}
	
	public static boolean isCloseRequested() {
		return display.isCloseRequested();
	}
	
	public static void destroy() {
		display.destroy();
	}
	
	public static void update() {
		display.update();
	}

	public static void setFullscreen(boolean b) {
		display.setFullScreen(b);
	}

	public static float getWidth() {
		return (float)display.getWidth();
	}

	public static float getHeight() {
		return (float)display.getHeight();
	}

	public static void setTitle(String title) {
		display.setTitle(title);
	}
	
	public static alex9932.utils.Display getDisplay() {
		return display;
	}

	public static boolean isFullscreen() {
		return display.isFullscreen();
	}

	public static int getFps() {
		return display.getFps();
	}
}