package alex9932.engine.main;

import org.lwjgl.opengl.GL11;

import alex9932.engine.game.Engine;
import alex9932.engine.render.Renderer;
import alex9932.engine.render.gui.GuiRenderer;
import alex9932.engine.render.gui.IGui;
import alex9932.utils.NVGUtils;
import alex9932.utils.Resource;
import alex9932.utils.gl.Shader;
import alex9932.utils.gl.texture.Texture;

public class Gui implements IGui {
	private static final Texture texture;
	
	static {
		texture = new Texture(Resource.getTexture("default.png"));
		NVGUtils.registerFont(Resource.getFont("arial.ttf"), "font2");
	}
	
	@Override
	public void render(Shader shader) {
		shader.loadInt("has_texture", 1);
		texture.connectToAndBind(0);
		//GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Main.engine.renderer.gbuffer.getPositions());
		GL11.glColor3f(1, 1, 1);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2d(0, 0);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2d(100, 0);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2d(100, 100);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2d(0, 100);
		GL11.glEnd();
		GL11.glColor3f(1, 1, 1);
		
		Main.engine.renderer.guirenderer.drawString(GuiRenderer.DEFAULT_FONT, 10, 120, "Easy render!");
		
		shader.loadInt("has_texture", 0);
		shader.stop();
		NVGUtils.begin(1280, 720);
		NVGUtils.color(1, 1, 1, 1);
		NVGUtils.drawString("Version: " + Engine._version, "font2", 10, 25, 20);
		NVGUtils.drawString("[opengl] Fps: " + Main.engine.getFps(), "font2", 10, 50, 20);
		NVGUtils.drawString("[opengl] Total triangles: " + Main.engine.renderer.trianglescount, "font2", 10, 75, 20);
		NVGUtils.drawString("[opengl] Renderer: " + Renderer._renderer, "font2", 10, 100, 20);
		NVGUtils.drawString("[opengl] Version: " + Renderer._version + " GLSL: " + Renderer._glslversion, "font2", 10, 125, 20);
		NVGUtils.drawString("X: " + Main.engine.renderer.getCamera().getX(), "font2", 10, 150, 20);
		NVGUtils.drawString("Y: " + Main.engine.renderer.getCamera().getY(), "font2", 10, 175, 20);
		NVGUtils.drawString("Z: " + Main.engine.renderer.getCamera().getZ(), "font2", 10, 200, 20);
		NVGUtils.drawString("AngleX: " + Main.engine.renderer.getCamera().getAngleX(), "font2", 200, 150, 20);
		NVGUtils.drawString("AngleY: " + Main.engine.renderer.getCamera().getAngleY(), "font2", 200, 175, 20);

		NVGUtils.drawString("[engine] timeupdate: " + Main.engine.getProfiler().getTime("timeupdate"), "font2", 10, 225, 15);
		NVGUtils.drawString("[engine] globalupdates: " + Main.engine.getProfiler().getTime("globalupdates"), "font2", 10, 245, 15);
		NVGUtils.drawString("[engine] render: " + Main.engine.getProfiler().getTime("render"), "font2", 10, 265, 15);
		NVGUtils.drawString("[engine] levelload: " + Main.engine.getProfiler().getTime("levelload"), "font2", 10, 285, 15);
		NVGUtils.end();
	}

	@Override
	public void show() {
		
	}

	@Override
	public void hide() {
		
	}
}