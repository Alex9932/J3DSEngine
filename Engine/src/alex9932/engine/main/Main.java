package alex9932.engine.main;

import java.io.File;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.Display;

import alex9932.engine.animation.AnimatedModel;
import alex9932.engine.animation.AnimatedModelLoader;
import alex9932.engine.animation.Animation;
import alex9932.engine.animation.AnimationLoader;
import alex9932.engine.game.AnimatedGameObject;
import alex9932.engine.game.Engine;
import alex9932.engine.game.GameObject;
import alex9932.engine.game.IGameImpl;
import alex9932.engine.physics.Body;
import alex9932.engine.physics.Material;
import alex9932.utils.FmlLoader;
import alex9932.utils.IKeyListener;
import alex9932.utils.Resource;
import alex9932.utils.gl.Vao;
import alex9932.utils.gl.Vbo;
import alex9932.utils.gl.texture.Texture;

public class Main extends IGameImpl implements IKeyListener{
	private static Engine engine;
	private Vao vao;
	private Texture texture;
	private Texture specular;

	@Override
	public void startup() throws Exception {
		Display.getDisplay().getEventSystem().addKeyListener(this);
		
		FmlLoader mdl = new FmlLoader(Resource.getModel("box.fml"));
		vao = new Vao();
		vao.setIndices(mdl.getInds());
		vao.put(new Vbo(0, 3, mdl.getVerts()));
		vao.put(new Vbo(1, 2, mdl.getTextureCoord()));
		vao.put(new Vbo(2, 3, mdl.getNormals()));
		texture = new Texture(Resource.getTexture("default.png"));
		specular = new Texture(Resource.getTexture("specular.png"));
	}
	
	@Override
	public void onLevelLoaded(String level) {
		Animation animation = AnimationLoader.loadAnimation(new File(Resource.getModel("model.dae")));
		AnimatedModel animModel = AnimatedModelLoader.loadEntity(Resource.getModel("model.dae"), Resource.getTexture("diffuse.png"));
		Body body = engine.physics.createTMeshBody(-1, 10, 0, 5, 0, animModel.getEntityData().getMeshData().getVertices(), animModel.getEntityData().getMeshData().getIndices(), Material.METAL);
		engine.physics.setFixed(body.getGeom());
		AnimatedGameObject object = new AnimatedGameObject(body, animModel);
		object.doAnimation(animation);
		engine.scene.add(object);
		engine.renderer.renderGui(new Gui());
	}
	
	public static void main(String[] args) throws Exception {
		engine = new Engine(new Main());
		engine.run();
	}

	@Override
	public void keyPressed(int key) {
		if(key == GLFW.GLFW_KEY_Q) {
			double x = engine.renderer.getCamera().getX();
			double y = engine.renderer.getCamera().getY();
			double z = engine.renderer.getCamera().getZ();
			GameObject obj = new GameObject(engine.physics.createBoxBody(15, 13, x, y, z, 2, 2, 2, Material.METAL), texture, specular, vao);
			engine.scene.add(obj);
		}
		
		if(key == GLFW.GLFW_KEY_ESCAPE) {
			engine.shutdown();
		}
		
		if(key == GLFW.GLFW_KEY_F11) {
			if(!Display.isFullscreen()) {
				Display.setFullscreen(true);
			} else {
				Display.setFullscreen(false);
			}
		}
	}

	@Override
	public void keyReleased(int key) {
		
	}
}