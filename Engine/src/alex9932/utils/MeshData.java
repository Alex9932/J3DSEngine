package alex9932.utils;

public class MeshData {
	private float[] vertices;
	private float[] normals;
	private float[] texCoords;
	private float[] tangents;
	private int[] indices;

	public MeshData(float[] vertices, float[] normals, float[] texCoords, float[] tangents, int[] indices) {
		this.vertices = vertices;
		this.texCoords = texCoords;
		this.normals = normals;
		this.tangents = tangents;
		this.indices = indices;
	}
	
	public float[] getVertices() {
		return vertices;
	}
	
	public float[] getTexCoords() {
		return texCoords;
	}
	
	public float[] getNormals() {
		return normals;
	}
	
	public float[] getTangents() {
		return tangents;
	}
	
	public int[] getIndices() {
		return indices;
	}
}