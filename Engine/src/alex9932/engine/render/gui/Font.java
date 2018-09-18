package alex9932.engine.render.gui;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

public class Font {
	public class Glyph {
		public final int width;
		public final int height;
		public final int x;
		public final int y;
		public final float advance;

		public Glyph(int width, int height, int x, int y, float advance) {
			this.width = width;
			this.height = height;
			this.x = x;
			this.y = y;
			this.advance = advance;
		}
	}

	private final Map<Character, Glyph> glyphs;

	private int texture;
	private int fontHeight;
	private int texWidth;
	private int texHeight;

	public Font(java.awt.Font font, boolean antiAlias) {
		glyphs = new HashMap<>();
		texture = createFontTexture(font, antiAlias);
	}

	public Font(String font, float size) {
		this(getFont(font, size), false);
	}

	private static java.awt.Font getFont(String fileName, float size) {
		try {
			java.awt.Font font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, new File(fileName)).deriveFont(size);
			return font;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private int createFontTexture(java.awt.Font font, boolean antiAlias) {
		int imageWidth = 0;
		int imageHeight = 0;

		for (int i = 32; i < 256; i++) {
			if (i == 127) {
				continue;
			}
			char c = (char) i;
			BufferedImage ch = createCharImage(font, c, antiAlias);
			if (ch == null) {
				continue;
			}
			imageWidth += ch.getWidth();
			imageHeight = Math.max(imageHeight, ch.getHeight());
		}

		texWidth = imageWidth;
		texHeight = imageHeight;
		
		fontHeight = imageHeight;
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();

		int x = 0;
		for (int i = 32; i < 256; i++) {
			if (i == 127) {
				continue;
			}
			char c = (char) i;
			BufferedImage charImage = createCharImage(font, c, antiAlias);
			if (charImage == null) {
				continue;
			}

			int charWidth = charImage.getWidth();
			int charHeight = charImage.getHeight();
			Glyph ch = new Glyph(charWidth, charHeight, x, image.getHeight() - charHeight, 0f);
			g.drawImage(charImage, x, 0, null);
			x += ch.width;
			glyphs.put(c, ch);
		}

		AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
		transform.translate(0, -image.getHeight());
		AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = operation.filter(image, null);
		int width = image.getWidth();
		int height = image.getHeight();
		int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);
		ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int pixel = pixels[i * width + j];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}
		buffer.flip();

		this.texture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.4f);
		
		MemoryUtil.memFree(buffer);
		return texture;
	}

	private BufferedImage createCharImage(java.awt.Font font, char c, boolean antiAlias) {
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		if (antiAlias) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		g.dispose();
		int charWidth = metrics.charWidth(c);
		int charHeight = metrics.getHeight();
		if (charWidth == 0) {
			return null;
		}
		image = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
		if (antiAlias) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.setFont(font);
		g.setPaint(java.awt.Color.WHITE);
		g.drawString(String.valueOf(c), 0, metrics.getAscent());
		g.dispose();
		return image;
	}

	public int getWidth(CharSequence text) {
		int width = 0;
		int lineWidth = 0;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '\n') {
				width = Math.max(width, lineWidth);
				lineWidth = 0;
				continue;
			}
			if (c == '\r') {
				continue;
			}
			Glyph g = glyphs.get(c);
			lineWidth += g.width;
		}
		width = Math.max(width, lineWidth);
		return width;
	}

	public int getHeight(CharSequence text) {
		int height = 0;
		int lineHeight = 0;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '\n') {
				height += lineHeight;
				lineHeight = 0;
				continue;
			}
			if (c == '\r') {
				continue;
			}
			Glyph g = glyphs.get(c);
			lineHeight = Math.max(lineHeight, g.height);
		}
		height += lineHeight;
		return height;
	}

	public void drawText(CharSequence text, float x, float y) {
		int textHeight = getHeight(text);

		float drawX = x;
		float drawY = y;
		if (textHeight > fontHeight) {
			drawY += textHeight - fontHeight;
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glBegin(GL11.GL_TRIANGLES);
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == '\n') {
				drawY -= fontHeight;
				drawX = x;
				continue;
			}
			if (ch == '\r') {
				continue;
			}
			Glyph g = glyphs.get(ch);
			
			drawTextureRegion(drawX, drawY, g.x, g.y, g.width, g.height);
			drawX += g.width;
		}
		GL11.glEnd();
	}
	
	private void drawTextureRegion(float xpos, float ypos, int x, int y, int w, int h) {
		float x0 = xpos;
		float y0 = ypos;
		float x1 = xpos + w;
		float y1 = ypos + h;
		
		float s0 = (float)x / (float)texWidth;
		float t0 = (float)y / (float)texHeight;
		float s1 = ((float)x + (float)w) / (float)texWidth;
		float t1 = ((float)y + (float)h) / (float)texHeight;
		//System.out.println(x0 + " " + y0 + " " + x1 + " " + y1 + " " + s0 + " " + t0 + " " + s1 + " " + t1);
		
		GL11.glTexCoord2f(s0, t0);
		GL11.glVertex2f(x0, y0);
		GL11.glTexCoord2f(s1, t0);
		GL11.glVertex2f(x1, y0);
		GL11.glTexCoord2f(s1, t1);
		GL11.glVertex2f(x1, y1);
		GL11.glTexCoord2f(s1, t1);
		GL11.glVertex2f(x1, y1);
		GL11.glTexCoord2f(s0, t1);
		GL11.glVertex2f(x0, y1);
		GL11.glTexCoord2f(s0, t0);
		GL11.glVertex2f(x0, y0);
	}

	public void dispose() {
		GL11.glDeleteTextures(this.texture);
	}
}