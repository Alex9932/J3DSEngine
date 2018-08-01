package alex9932.engine.animation;

import java.io.File;

import alex9932.utils.gl.Vao;
import alex9932.utils.gl.Vbo;
import alex9932.utils.gl.texture.Texture;
import dae.collada.AnimatedModelData;
import dae.collada.ColladaLoader;
import dae.collada.JointData;
import dae.collada.MeshData;
import dae.collada.SkeletonData;

public class AnimatedModelLoader {


	/**
	 * Creates an AnimatedEntity from the data in an entity file. It loads up
	 * the collada model data, stores the extracted data in a VAO, sets up the
	 * joint heirarchy, and loads up the entity's texture.
	 * 
	 * @param entityFile
	 *            - the file containing the data for the entity.
	 * @return The animated entity (no animation applied though)
	 */
	public static AnimatedModel loadEntity(String modelFile, String textureFile) {
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(new File(modelFile), AnimationConstants.MAX_WEIGHTS);
		Vao model = createVao(entityData.getMeshData(), modelFile);
		Texture texture = loadTexture(textureFile);
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = createJoints(skeletonData.headJoint);
		return new AnimatedModel(entityData, model, texture, headJoint, skeletonData.jointCount);
	}

	/**
	 * Loads up the diffuse texture for the model.
	 * 
	 * @param textureFile
	 *            - the texture file.
	 * @return The diffuse texture.
	 */
	private static Texture loadTexture(String textureFile) {
		Texture diffuseTexture = new Texture(textureFile);//Texture.newTexture(textureFile).anisotropic().create();
		return diffuseTexture;
	}

	/**
	 * Constructs the joint-hierarchy skeleton from the data extracted from the
	 * collada file.
	 * 
	 * @param data
	 *            - the joints data from the collada file for the head joint.
	 * @return The created joint, with all its descendants added.
	 */
	private static Joint createJoints(JointData data) {
		Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform);
		for (JointData child : data.children) {
			joint.addChild(createJoints(child));
		}
		return joint;
	}

	/**
	 * Stores the mesh data in a VAO.
	 * 
	 * @param data
	 *            - all the data about the mesh that needs to be stored in the
	 *            VAO.
	 * @param file 
	 * @return The VAO containing all the mesh data for the model.
	 */
	private static Vao createVao(MeshData data, String file) {
		Vao vao = new Vao(file); //Vao.create();
		vao.bind();
		vao.setIndices(data.getIndices());               //vao.createIndexBuffer(data.getIndices());
		vao.put(new Vbo(0, 3, data.getVertices()));      //vao.createAttribute(0, data.getVertices(), 3);
		vao.put(new Vbo(1, 2, data.getTextureCoords())); //vao.createAttribute(1, data.getTextureCoords(), 2);
		vao.put(new Vbo(2, 3, data.getNormals()));       //vao.createAttribute(2, data.getNormals(), 3);
		vao.put(new Vbo(3, 3, data.getJointIds()));      //vao.createIntAttribute(3, data.getJointIds(), 3);
		vao.put(new Vbo(4, 3, data.getVertexWeights())); //vao.createAttribute(4, data.getVertexWeights(), 3);
		vao.unbind();
		return vao;
	}

}
