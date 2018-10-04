package alex9932.utils.gl.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

public class DefaultTextureDecoder implements ITextureDecoder{
	@Override
	public TextureData decode(String path) throws IOException {
		BufferedImage image = ImageIO.read(new File(path));
		int width = image.getWidth();
		int height = image.getHeight();

		byte[] pixels = new byte[width * height * 4];
		int i = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int color = image.getRGB(x, y);
				pixels[(i * 4) + 0] = (byte) ((color >> 16) & 0xFF);
				pixels[(i * 4) + 1] = (byte) ((color >> 8) & 0xFF);
				pixels[(i * 4) + 2] = (byte) (color & 0xFF);
				pixels[(i * 4) + 3] = (byte) ((color >> 24) & 0xFF);
				i++;
			}
		}

		ByteBuffer buffer = BufferUtils.createByteBuffer(pixels.length);
		buffer.put(pixels);
		buffer.flip();

		TextureData data = new TextureData();
		data.data = buffer;
		data.width = width;
		data.height = height;
		return data;
	}
}