package alex9932.utils;

import java.io.DataInputStream;
import java.io.FileInputStream;

//Fox engine model loader (FML)
public class FmlLoader {
	private float[] vertices;
	private float[] normals;
	private float[] tangents;
	private float[] texcoords;
	private int[] indices;
	
	public FmlLoader(String path) {
		System.out.println("[Model loader] Loading model: " + path + "... ");
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(path));

			//Read header
			//System.out.println("[Fml] Reading header...");
			vertices = new float[dis.readInt()];
			normals = new float[dis.readInt()];
			tangents = new float[dis.readInt()];
			texcoords = new float[dis.readInt()];
			indices = new int[dis.readInt()];
			long total = dis.readLong();
			
			if(total != ((long)vertices.length + (long)normals.length + (long)tangents.length + (long)texcoords.length + (long)indices.length)) {
				System.out.println("[Fml] Header error!");
			}

			//System.out.println("[Fml] Reading data...");
			for (int i = 0; i < vertices.length; i++) {
				vertices[i] = dis.readFloat();
			}
			for (int i = 0; i < normals.length; i++) {
				normals[i] = dis.readFloat();
			}
			for (int i = 0; i < tangents.length; i++) {
				tangents[i] = dis.readFloat();
			}
			for (int i = 0; i < texcoords.length; i++) {
				texcoords[i] = dis.readFloat();
			}
			for (int i = 0; i < indices.length; i++) {
				indices[i] = dis.readInt();
			}
			
			dis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int[] getInds() {
		return indices;
	}
	
	public float[] getNormals() {
		return normals;
	}
	
	public float[] getTangents() {
		return tangents;
	}
	
	public float[] getTextureCoord() {
		return texcoords;
	}
	
	public float[] getVerts() {
		return vertices;
	}
}