package alex9932.utils.gl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Vbo {
	private static final int BYTES_PER_FLOAT = 4;
	private static final int BYTES_PER_INT = 4;
	
	private int id;
	private int buffer;
	private int coordSize;
	private FloatBuffer data;
	private IntBuffer datai;

	public Vbo(int id, int coordSize, float[] data) {
		this.id = id;
		this.coordSize = coordSize;
		this.data = convertData(data);
	}

	public Vbo(int id, int coordSize, int[] data) {
		this.id = id;
		this.coordSize = coordSize;
		this.datai = convertDatai(data);
	}

	private IntBuffer convertDatai(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private FloatBuffer convertData(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	public void create() {
		this.buffer = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.buffer);
		if(this.data != null) {
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.data, GL15.GL_STATIC_DRAW);
			GL20.glVertexAttribPointer(this.id, this.coordSize, GL11.GL_FLOAT, false, this.coordSize * BYTES_PER_FLOAT, 0);
		} else {
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.datai, GL15.GL_STATIC_DRAW);
			GL30.glVertexAttribIPointer(this.id, this.coordSize, GL11.GL_INT, this.coordSize * BYTES_PER_INT, 0);
		}
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void cleanUp(){
		GL15.glDeleteBuffers(this.buffer);
	}

	public void destroy() {
		GL15.glDeleteBuffers(this.buffer);
	}
}