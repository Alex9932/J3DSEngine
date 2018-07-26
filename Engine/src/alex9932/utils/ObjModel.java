package alex9932.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class ObjModel {
	private float[] verts;
	private float[] textureCoord;
	private float[] normalsArray;
	private float[] tangentsArray;
	private int[] inds;

	public ObjModel(String path){
		System.out.print("[Model loader] Loading model: " + path + "... ");
		FileReader isr = null;
		File objFile = new File(path);
		try {
			isr = new FileReader(objFile);
		} catch (FileNotFoundException e) {
			System.err.println("FAILED!");
			System.err.println("File not found!");
		}
		BufferedReader reader = new BufferedReader(isr);
		String line;

		ArrayList<OBJVertex> vertices = new ArrayList<OBJVertex>();
		ArrayList<Vector2f> textures = new ArrayList<Vector2f>();
		ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		try {
			while (true) {
				line = reader.readLine();
				if (line.startsWith("v ")) {
					String[] currentLine = line.split(" ");
					Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]),
							(float) Float.valueOf(currentLine[3]));
					OBJVertex newVertex = new OBJVertex(vertices.size(), vertex);
					vertices.add(newVertex);
				} else if (line.startsWith("vt ")) {
					String[] currentLine = line.split(" ");
					Vector2f tex = new Vector2f((float) Float.valueOf(currentLine[1]), (float) Float.valueOf(currentLine[2]));
					textures.add(tex);
				} else if (line.startsWith("vn ")) {
					String[] currentLine = line.split(" ");
					Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]),
							(float) Float.valueOf(currentLine[3]));
					normals.add(normal);
				}else if (line.startsWith("f ")) {
					break;
				}
			}
			
			while (line != null && line.startsWith("f ")) {
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				OBJVertex v0 = processVertex(vertex1, vertices, indices);
				OBJVertex v1 = processVertex(vertex2, vertices, indices);
				OBJVertex v2 = processVertex(vertex3, vertices, indices);
				calculateTangents(v0, v1, v2, textures);
				line = reader.readLine();
			}
			reader.close();
		}catch(Exception e){}

		verts = new float[vertices.size() * 3];
		textureCoord = new float[vertices.size() * 3];
		normalsArray = new float[vertices.size() * 3];
		tangentsArray = new float[vertices.size() * 3];
		inds = convertIndicesListToArray(indices);
		convertDataToArrays(vertices, textures, normals, verts, textureCoord, normalsArray, tangentsArray);
		
		System.out.println("OK");
		
	}

	public float[] getVerts() {
		return verts;
	}
	
	public float[] getTextureCoord() {
		return textureCoord;
	}
	
	public float[] getNormalsArray() {
		return normalsArray;
	}
	
	public float[] getTangentsArray() {
		return tangentsArray;
	}
	
	public int[] getInds() {
		return inds;
	}

	private static int[] convertIndicesListToArray(ArrayList<Integer> indices) {
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	private static float convertDataToArrays(ArrayList<OBJVertex> vertices, ArrayList<Vector2f> textures, ArrayList<Vector3f> normals, float[] verticesArray, float[] texturesArray, float[] normalsArray, float[] tangentsArray) {
		float furthestPoint = 0;
		for (int i = 0; i < vertices.size(); i++) {
			OBJVertex currentVertex = vertices.get(i);
			if (currentVertex.getLength() > furthestPoint) {
				furthestPoint = currentVertex.getLength();
			}
			Vector3f position = currentVertex.getPosition();
			
			try{
				Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
				texturesArray[i * 2] = textureCoord.x;
				texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			}catch(Exception e){}
			
			Vector3f normalVector = new Vector3f(0, 1, 0);
			try{
				normalVector = normals.get(currentVertex.getNormalIndex());
			}catch(Exception e){}
			currentVertex.averageTangents();
			Vector3f tangent = currentVertex.getAverageTangent();
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
			tangentsArray[i * 3] = tangent.x;
			tangentsArray[i * 3 + 1] = tangent.y;
			tangentsArray[i * 3 + 2] = tangent.z;

		}
		return furthestPoint;
	}

	private static void calculateTangents(OBJVertex v0, OBJVertex v1, OBJVertex v2, ArrayList<Vector2f> textures) {
		Vector3f delatPos1 = Vector3f.sub(v1.getPosition(), v0.getPosition(), null);
		Vector3f delatPos2 = Vector3f.sub(v2.getPosition(), v0.getPosition(), null);
		try {
			Vector2f uv0 = textures.get(v0.getTextureIndex());
			Vector2f uv1 = textures.get(v1.getTextureIndex());
			Vector2f uv2 = textures.get(v2.getTextureIndex());
			Vector2f deltaUv1 = Vector2f.sub(uv1, uv0, null);
			Vector2f deltaUv2 = Vector2f.sub(uv2, uv0, null);
		
			float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
			delatPos1.scale(deltaUv2.y);
			delatPos2.scale(deltaUv1.y);
			Vector3f tangent = Vector3f.sub(delatPos1, delatPos2, null);
			tangent.scale(r);
			v0.addTangent(tangent);
			v1.addTangent(tangent);
			v2.addTangent(tangent);
		
		} catch (Exception e) {
		}
	}
	
	private static OBJVertex processVertex(String[] vertex, ArrayList<OBJVertex> vertices, ArrayList<Integer> indices) {
		int index = Integer.parseInt(vertex[0]) - 1;
		OBJVertex currentVertex = vertices.get(index);
		int textureIndex = 0;
		try {
			textureIndex = Integer.parseInt(vertex[1]) - 1;
		} catch (Exception e) {
		}
		int normalIndex = Integer.parseInt(vertex[2]) - 1;
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			indices.add(index);
			return currentVertex;
		} else {
			return dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices, vertices);
		}
	}
	
	private static OBJVertex dealWithAlreadyProcessedVertex(OBJVertex previousVertex, int newTextureIndex, int newNormalIndex, ArrayList<Integer> indices, ArrayList<OBJVertex> vertices) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
			return previousVertex;
		} else {
			OBJVertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex,
						newNormalIndex, indices, vertices);
			} else {
				OBJVertex duplicateVertex = new OBJVertex(vertices.size(), previousVertex.getPosition());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
				return duplicateVertex;
			}

		}
	}

	public MeshData getData() {
		return new MeshData(verts, normalsArray, textureCoord, tangentsArray, inds);
	}
}

class OBJVertex {
	private static final int NO_INDEX = -1;
	private Vector3f position;
	private int textureIndex = NO_INDEX;
	private int normalIndex = NO_INDEX;
	private OBJVertex duplicateVertex = null;
	private int index;
	private float length;
	private List<Vector3f> tangents = new ArrayList<Vector3f>();
	private Vector3f averagedTangent = new Vector3f(0, 0, 0);
	
	public OBJVertex(int index,Vector3f position){
		this.index = index;
		this.position = position;
		this.length = position.length();
	}
	
	public void addTangent(Vector3f tangent){
		tangents.add(tangent);
	}
	
	public void averageTangents(){
		if(tangents.isEmpty()){
			return;
		}
		for(Vector3f tangent : tangents){
			averagedTangent = Vector3f.add(averagedTangent, tangent, null);
		}
		try{
			averagedTangent.normalise();
		}catch(Exception e){}
	}
	
	public Vector3f getAverageTangent(){
		return averagedTangent;
	}
	
	public int getIndex(){
		return index;
	}
	
	public float getLength(){
		return length;
	}
	
	public boolean isSet(){
		return textureIndex!=NO_INDEX && normalIndex!=NO_INDEX;
	}
	
	public boolean hasSameTextureAndNormal(int textureIndexOther,int normalIndexOther){
		return textureIndexOther==textureIndex && normalIndexOther==normalIndex;
	}
	
	public void setTextureIndex(int textureIndex){
		this.textureIndex = textureIndex;
	}
	
	public void setNormalIndex(int normalIndex){
		this.normalIndex = normalIndex;
	}

	public Vector3f getPosition() {
		return position;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public int getNormalIndex() {
		return normalIndex;
	}

	public OBJVertex getDuplicateVertex() {
		return duplicateVertex;
	}

	public void setDuplicateVertex(OBJVertex duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}
}
