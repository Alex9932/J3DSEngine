package alex9932.engine.game;

import org.lwjgl.util.vector.Matrix4f;

import alex9932.engine.physics.Body;
import alex9932.utils.MatMath;
import alex9932.utils.gl.Vao;
import alex9932.utils.gl.texture.Texture;

public class GameObject {
	private Matrix4f model_matrix;
	private Texture texture;
	private Texture specular;
	private Body body;
	private Vao vao;
	private float x;
	private float y;
	private float z;
	
	public GameObject(Body body, Texture texture, Vao model) {
		this(body, texture, null, model);
	}
	
	public GameObject(Body body, Texture texture, Texture specular, Vao vao) {
		this.vao = vao;
		this.body = body;
		this.texture = texture;
		this.specular = specular;
		if(body != null) {
			this.x = (float)body.getGeom().getPosition().get0();
			this.y = (float)body.getGeom().getPosition().get1();
			this.z = (float)body.getGeom().getPosition().get2();
			this.model_matrix = MatMath.createModelMatrixFromBody(body);
		} else {
			this.x = 0;
			this.y = 1;
			this.z = 0;
			this.model_matrix = MatMath.createModelMatrix(x, y, z, 0, 0, 0, 1);
		}
	}

	public void update() {
		if(body != null) {
			this.model_matrix = MatMath.createModelMatrixFromBody(body);
		}else{
			this.model_matrix = MatMath.createModelMatrix(x, y, z, 0, 0, 0, 1);
		}
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getZ() {
		return z;
	}
	
	public Vao getVao() {
		return vao;
	}
	
	public Body getBody() {
		return body;
	}
	
	public Matrix4f getModelMatrix() {
		return model_matrix;
	}
	
	public Texture getTexture() {
		return texture;
	}

	public Texture getSpecular() {
		return specular;
	}
}