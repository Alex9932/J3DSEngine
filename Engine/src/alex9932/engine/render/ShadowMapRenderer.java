package alex9932.engine.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import alex9932.engine.game.AnimatedGameObject;
import alex9932.engine.game.GameObject;
import alex9932.engine.game.Scene;

public class ShadowMapRenderer {
	private static final int SHADOW_MAP_SIZE = (int)(4096 * 2);

	private ShadowFrameBuffer shadowFbo;
	private ShadowShader shader;
	private ShadowBox shadowBox;
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f lightViewMatrix = new Matrix4f();
	private Matrix4f projectionViewMatrix = new Matrix4f();
	private Matrix4f offset = createOffset();

	public ShadowMapRenderer(ICamera camera) {
		shader = new ShadowShader();
		shadowBox = new ShadowBox(lightViewMatrix, camera);
		shadowFbo = new ShadowFrameBuffer(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE);
	}

	public void render(Scene scene, Vector3f lightDirection) {
		GL.cullFace(GL.GL_FRONT);
		shadowBox.update();
		prepare(lightDirection, shadowBox);

		for (int i = 0; i < scene.getObjects().size(); i++) {
			GameObject entity = scene.getObjects().get(i);
			prepareInstance(entity);

			if(entity.getVao().isIgnoreCulling()){
				GL11.glDisable(GL11.GL_CULL_FACE);
			}
			entity.getVao().bind();
			if(entity instanceof AnimatedGameObject){
				shader.loadInt("model_type", Renderer.TYPE_MODEL_ANIMATED);
				GL20.glEnableVertexAttribArray(0);
				GL20.glEnableVertexAttribArray(1);
				GL20.glEnableVertexAttribArray(2);
				GL20.glEnableVertexAttribArray(3);
				GL20.glEnableVertexAttribArray(4);
				GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getVao().getIndices());
				GL20.glDisableVertexAttribArray(0);
				GL20.glDisableVertexAttribArray(1);
				GL20.glDisableVertexAttribArray(2);
				GL20.glDisableVertexAttribArray(3);
				GL20.glDisableVertexAttribArray(4);
			} else {
				shader.loadInt("model_type", Renderer.TYPE_MODEL_STATIC);
				GL20.glEnableVertexAttribArray(0);
				GL20.glEnableVertexAttribArray(1);
				GL20.glEnableVertexAttribArray(2);
				GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getVao().getIndices());
				GL20.glDisableVertexAttribArray(0);
				GL20.glDisableVertexAttribArray(1);
				GL20.glDisableVertexAttribArray(2);
			}
			entity.getVao().unbind();
			if(entity.getVao().isIgnoreCulling()){
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL.cullFace(GL.GL_FRONT);
			}
		}
		
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);

		GL.cullFace(GL.GL_BACK);
		finish();
	}

	private void prepareInstance(GameObject entity) {
		Matrix4f mvpMatrix = Matrix4f.mul(projectionViewMatrix, entity.getModelMatrix(), null);
		shader.loadMvpMatrix(mvpMatrix);
		if(entity instanceof AnimatedGameObject) {
			AnimatedGameObject obj = (AnimatedGameObject)entity;
			for (int k = 0; k < obj.getJointTransform().length; k++) {
				shader.loadMatrix4f("jointTransforms[" + k + "]", obj.getJointTransform()[k]);
			}
		}
		entity.getTexture().connectToAndBind(0);
	}

	public Matrix4f getToShadowMapSpaceMatrix() {
		return Matrix4f.mul(offset, projectionViewMatrix, null);
	}

	public void cleanUp() {
		shadowFbo.cleanUp();
	}
	
	public int getShadowMap() {
		return shadowFbo.getShadowMap();
	}

	protected Matrix4f getLightSpaceTransform() {
		return lightViewMatrix;
	}

	private void prepare(Vector3f lightDirection, ShadowBox box) {
		updateOrthoProjectionMatrix(box.getWidth(), box.getHeight(), box.getLength());
		updateLightViewMatrix(lightDirection, box.getCenter());
		Matrix4f.mul(projectionMatrix, lightViewMatrix, projectionViewMatrix);
		shadowFbo.bindFrameBuffer();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		shader.start();
	}

	private void finish() {
		shader.stop();
		shadowFbo.unbindFrameBuffer();
	}

	private void updateLightViewMatrix(Vector3f direction, Vector3f center) {
		direction.normalise();
		center.negate();
		lightViewMatrix.setIdentity();
		float pitch = (float) Math.acos(new Vector2f(direction.x, direction.z).length());
		Matrix4f.rotate(pitch, new Vector3f(1, 0, 0), lightViewMatrix, lightViewMatrix);
		float yaw = (float) Math.toDegrees(((float) Math.atan(direction.x / direction.z)));
		yaw = direction.z > 0 ? yaw - 180 : yaw;
		Matrix4f.rotate((float) -Math.toRadians(yaw), new Vector3f(0, 1, 0), lightViewMatrix, lightViewMatrix);
		Matrix4f.translate(center, lightViewMatrix, lightViewMatrix);
	}

	private void updateOrthoProjectionMatrix(float width, float height, float length) {
		projectionMatrix.setIdentity();
		projectionMatrix.m00 = 2f / width;
		projectionMatrix.m11 = 2f / height;
		projectionMatrix.m22 = -2f / length;
		projectionMatrix.m33 = 1;
	}

	private static Matrix4f createOffset() {
		Matrix4f offset = new Matrix4f();
		offset.translate(new Vector3f(0.5f, 0.5f, 0.5f));
		offset.scale(new Vector3f(0.5f, 0.5f, 0.5f));
		return offset;
	}
}
