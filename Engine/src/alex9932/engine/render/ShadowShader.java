package alex9932.engine.render;

import org.lwjgl.util.vector.Matrix4f;

import alex9932.engine.animation.AnimationConstants;
import alex9932.utils.gl.Shader;

public class ShadowShader extends Shader {
	private static final String VERTEX_FILE = "gamedata/shaders/shadow.vs.h";
	private static final String FRAGMENT_FILE = "gamedata/shaders/shadow.ps.h";

	protected ShadowShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	public void bindUniformLocations() {
		super.createUniformLocation("mvp");
	}
	
	public void loadMvpMatrix(Matrix4f mvpMatrix){
		super.loadMatrix4f("mvp", mvpMatrix);
		for (int i = 0; i < AnimationConstants.MAX_JOINTS; i++) {
			this.createUniformLocation("jointTransforms[" + i + "]");
		}
		this.createUniformLocation("model_type");
	}

	@Override
	public void bindAttribs() {
		super.bindAttribute(0, "in_position");
		super.bindAttribute(1, "texcoords");
		this.bindAttribute(2, "in_normal");
		this.bindAttribute(3, "in_jointIndices");
		this.bindAttribute(4, "in_weights");
	}
}