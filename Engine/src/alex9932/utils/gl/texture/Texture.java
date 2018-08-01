package alex9932.utils.gl.texture;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class Texture {
	private static final ITextureDecoder DEFAULT_DECODER = new DefaultTextureDecoder();
	
	public static final int REPEAT = GL11.GL_REPEAT;
	public static final int CLAMP_TO_EDGE = GL12.GL_CLAMP_TO_EDGE;
	private int id;
	private int width;
	private int height;

	private String path;

	public Texture(int tex){
		this.id = tex;
	}

	public Texture(String path){
		this(path, DEFAULT_DECODER, GL12.GL_CLAMP_TO_EDGE);
	}

	public Texture(String path, ITextureDecoder decoder){
		this(path, decoder, GL12.GL_CLAMP_TO_EDGE);
	}

	public Texture(String path, int glMode) {
		this(path, DEFAULT_DECODER, glMode);
	}
	
	public Texture(String path, ITextureDecoder decoder, int glMode) {
		System.out.print("[Texture] Loading texture: " + path + " ...  ");
		this.path = path;
		TextureData data = decodeTextureFile(path, decoder);
		this.width = data.width;
		this.height = data.height;
		loadToGL(data.width, data.height, data.data, glMode);
		System.out.println("OK!");
	}

	public Texture(String[] paths){
		this(paths, DEFAULT_DECODER);
	}
	
	public Texture(String[] paths, ITextureDecoder decoder){
		System.out.print("[Texture] Loading textures: " + paths[0] + " ...  ");
		this.width = 128;
		this.height = 128;
		loadCubeMap(paths, decoder);
		System.out.println("OK!");
	}

	public int getId() {
		return this.id;
	}

	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);
	}

	public void bindAsCubeMap() {
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, this.id);
	}

	public void connectTo(int i) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
	}

	public void connectToAndBind(int i) {
		connectTo(i);
		bind();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	private void loadToGL(int width, int height, ByteBuffer buffer, int glMode) {
		//GL11.glEnable(GL11.GL_TEXTURE_2D);
		this.id = GL11.glGenTextures();
		this.bind();
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, glMode);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, glMode);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.4f);
	}
	
	public void loadCubeMap(String[] textureFiles, ITextureDecoder decoder) {
		//GL11.glEnable(GL13.GL_TEXTURE_CUBE_MAP);
		this.id = GL11.glGenTextures();
		this.bindAsCubeMap();
		
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		for (int i = 0; i < textureFiles.length; i++) {
			TextureData data = decodeTextureFile(textureFiles[i], decoder);
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.width, data.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.data);
		}


		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.4f);
	}

	private TextureData decodeTextureFile(String path, ITextureDecoder decoder) {
		try {
			TextureData data = decoder.decode(path);
			return data;
		} catch (Exception e) {
			System.out.println("FAILED!");
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Texture){
			Texture tex = (Texture)obj;
			return (tex.id == this.id);
		}else{
			return false;
		}
	}

	public void delete() {
		GL11.glDeleteTextures(id);
	}

	public String getPath() {
		return path;
	}
}