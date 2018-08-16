package alex9932.engine.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import alex9932.utils.MatMath;
import alex9932.utils.Resource;
import alex9932.utils.gl.Shader;
import alex9932.utils.gl.Vao;
import alex9932.utils.gl.Vbo;
//import alex9932.utils.gl.texture.Texture;

public class Skybox extends Shader{
	private static final float SIZE = 500;
	private static final float[] VERTICES = {
		-1,  1, -1, -1, -1, -1,  1, -1, -1,  1, -1, -1,  1,  1, -1, -1,  1, -1,
		-1, -1,  1, -1, -1, -1, -1,  1, -1, -1,  1, -1, -1,  1,  1, -1, -1,  1,
		 1, -1, -1,  1, -1,  1,  1,  1,  1,  1,  1,  1,  1,  1, -1,  1, -1, -1,
		-1, -1,  1, -1,  1,  1,  1,  1,  1,  1,  1,  1,  1, -1,  1, -1, -1,  1,
		-1,  1, -1,  1,  1, -1,  1,  1,  1,  1,  1,  1, -1,  1,  1, -1,  1, -1,
		-1, -1, -1, -1, -1,  1,  1, -1, -1,  1, -1, -1, -1, -1,  1,  1, -1,  1
	};
	private static String[] TEXTURE_FILES = new String[6];

	private Vao vao;
	private String skyname;
	//private Texture texture;
	
	public Skybox(String skyname) {
		super(Resource.getShader("skybox.vs.h"), Resource.getShader("skybox.ps.h"));
		this.skyname = skyname;
		vao = new Vao(null);
		vao.put(new Vbo(0, 3, VERTICES));
		
		TEXTURE_FILES[0] = Resource.getTexture("skybox/" + this.skyname + "/negZ.png");
		TEXTURE_FILES[1] = Resource.getTexture("skybox/" + this.skyname + "/posZ.png");
		TEXTURE_FILES[2] = Resource.getTexture("skybox/" + this.skyname + "/posY.png");
		TEXTURE_FILES[3] = Resource.getTexture("skybox/" + this.skyname + "/negY.png");
		TEXTURE_FILES[4] = Resource.getTexture("skybox/" + this.skyname + "/posX.png");
		TEXTURE_FILES[5] = Resource.getTexture("skybox/" + this.skyname + "/negX.png");
		
		//texture = new Texture(TEXTURE_FILES);
	}
	
	public void render(ICamera camera) {
		Matrix4f matrix = MatMath.createViewMatrix(camera.getX(), camera.getY(), camera.getZ(), camera.getAngleX(), camera.getAngleY());
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		Matrix4f.scale(new Vector3f(SIZE, SIZE, SIZE), matrix, matrix);
        
		//texture.bindAsCubeMap();
		//texture.connectTo(0);
        
		start();
		loadMatrix4f("proj", camera.getProjection());
		loadMatrix4f("model", matrix);
		vao.bind();
		GL20.glEnableVertexAttribArray(0);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36);
		GL20.glDisableVertexAttribArray(0);
		vao.unbind();
		stop();
	}
	
	@Override
	public void bindAttribs() {
		this.bindAttribute(0, "in_position");
	}
	
	@Override
	public void bindUniformLocations() {
		this.createUniformLocation("proj");
		this.createUniformLocation("model");
	}
}