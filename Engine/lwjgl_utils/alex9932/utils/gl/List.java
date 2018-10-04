package alex9932.utils.gl;

import org.lwjgl.opengl.GL11;

public class List {
	private int list;
	
	public List() {
		this.list = GL11.glGenLists(1);
	}
	
	public void bind() {
		GL11.glNewList(list, GL11.GL_COMPILE);
	}
	
	public void end() {
		GL11.glEndList();
	}
	
	public void render() {
		GL11.glCallList(list);
	}
	
	public int getList() {
		return list;
	}

	public void remove() {
		GL11.glDeleteLists(list, 1);
	}
}