package alex9932.engine.game;

import org.lwjgl.util.vector.Matrix4f;

import alex9932.engine.animation.AnimatedModel;
import alex9932.engine.animation.Animation;
import alex9932.engine.physics.Body;

public class AnimatedGameObject extends GameObject{
	private AnimatedModel model;

	public AnimatedGameObject(Body body, AnimatedModel model) {
		super(body, model.getTexture(), model.getModel());
		this.model = model;
	}
	
	@Override
	public void update() {
		this.model.update();
		super.update();
	}
	
	public void doAnimation(Animation animation) {
		this.model.doAnimation(animation);
	}
	
	public Matrix4f[] getJointTransform() {
		return this.model.getJointTransforms();
	}
	
	public AnimatedModel getModel() {
		return model;
	}
}