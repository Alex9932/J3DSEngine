package alex9932.utils;

import java.io.DataOutputStream;
import java.io.FileOutputStream;

public class FmlWriter {
	public FmlWriter(String path, float[] vertices, float[] normals, float[] tangents, float[] texturecoords, int[] indices) {
		System.out.println("[Model loader] Writing model: " + path + "... ");
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(path));

			System.out.println("[Fml] Writing header...");
			long total = (long)vertices.length + (long)normals.length + (long)tangents.length + (long)texturecoords.length + (long)indices.length;
			dos.writeInt(vertices.length);
			dos.writeInt(normals.length);
			dos.writeInt(tangents.length);
			dos.writeInt(texturecoords.length);
			dos.writeInt(indices.length);
			dos.writeLong(total);

			System.out.println("[Fml] Writing data...");
			for (int i = 0; i < vertices.length; i++) {
				dos.writeFloat(vertices[i]);
			}
			for (int i = 0; i < normals.length; i++) {
				dos.writeFloat(normals[i]);
			}
			for (int i = 0; i < tangents.length; i++) {
				dos.writeFloat(tangents[i]);
			}
			for (int i = 0; i < texturecoords.length; i++) {
				dos.writeFloat(texturecoords[i]);
			}
			for (int i = 0; i < indices.length; i++) {
				dos.writeInt(indices[i]);
			}
			
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("[Fml] Done!");
	}
}