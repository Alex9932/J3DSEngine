package alex9932.utils.gl;

import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

public class Vao {
	private int id;
	private ArrayList<Vbo> vbos = new ArrayList<Vbo>();
	private IntBuffer indices;
	private boolean ignore_culling;
	private String path;

	public Vao(String path) {
		this.path = path;
		this.id = GL30.glGenVertexArrays();
	}
	
	public void put(Vbo vbo){
		bind();
		vbo.create();
		unbind();
		vbos.add(vbo);
	}
	
	public void bind(){
		GL30.glBindVertexArray(id);
	}
	
	public void unbind(){
		GL30.glBindVertexArray(0);
	}

	public void destroy() {
		for (int i = 0; i < vbos.size(); i++) {
			vbos.get(i).destroy();
		}
		GL30.glDeleteVertexArrays(this.id);
	}

	public void setIndices(int[] indices) {
		this.indices = BufferUtils.createIntBuffer(indices.length);
		this.indices.put(indices);
		this.indices.flip();
	}
	
	public IntBuffer getIndices() {
		return indices;
	}

	public void setIgnoreCulling(boolean b) {
		this.ignore_culling = b;
	}
	
	public boolean isIgnoreCulling() {
		return ignore_culling;
	}

	public String getPath() {
		return path;
	}
}