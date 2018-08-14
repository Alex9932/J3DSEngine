package alex9932.engine.render;

import org.json.JSONObject;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vector.Vector3f;

import alex9932.engine.animation.AnimationConstants;
import alex9932.engine.game.AnimatedGameObject;
import alex9932.engine.game.Camera;
import alex9932.engine.game.GameObject;
import alex9932.engine.game.Scene;
import alex9932.engine.render.gui.GuiRenderer;
import alex9932.engine.render.gui.IGui;
import alex9932.engine.render.pp.PostProcessing;
import alex9932.script.FileIO;
import alex9932.utils.IKeyListener;
import alex9932.utils.Resource;
import alex9932.utils.gl.Fbo;
import alex9932.utils.gl.Shader;

public class Renderer implements IKeyListener{
	public static final int TYPE_MODEL_STATIC = 0;
	public static final int TYPE_MODEL_ANIMATED = 1;
	public static final boolean IS_DEBUG = true;
	
	private ICamera camera;
	private Shader shader;
	private boolean isWireframe;
	private float angle = -1.0f;
	private Vector3f LIGHT_DIR = new Vector3f(angle, -1, 1);
	private ShadowMapRenderer shadowRenderer;
	private Fbo fbo;
	private Fbo msfbo;
	private GuiRenderer guirenderer;
	private IGui gui = null;
	public GBuffer gbuffer;
	
	public Renderer() {
		System.out.println("[Renderer] Starting up...");
		Display.getDisplay().getEventSystem().addKeyListener(this);
		
		if(IS_DEBUG) {
			GL11.glEnable(GL43.GL_DEBUG_OUTPUT);
			GL11.glEnable(GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS);
			GL43.glDebugMessageCallback(new GLDebugMessageCallbackI() {
				@Override
				public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
					System.out.println("[OPENGL] " + MemoryUtil.memUTF8(MemoryUtil.memByteBuffer(message, length)));
				}
			}, 0);
		}
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		
		JSONObject object = null;
		try {
			object = new JSONObject(FileIO.read(Resource.getConfig("creatures/camera.json")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Camera cam = new Camera((float)object.getDouble("fov"), (float)object.getDouble("near"), (float)object.getDouble("far"), 0, 5.5f, 10);
		cam.setSens((float)object.getDouble("sens"));
		cam.setSpeed((float)object.getDouble("speed"));
		camera = cam;
		shader = new Shader(Resource.getShader("shader.vs.h"), Resource.getShader("shader.ps.h")) {
			@Override
			public void bindAttribs() {
				this.bindFragOutput(0, "out_color");
				this.bindFragOutput(1, "out_normal");
				this.bindFragOutput(2, "out_specular");
				this.bindAttribute(0, "in_position");
				this.bindAttribute(1, "in_textureCoords");
				this.bindAttribute(2, "in_normal");
				this.bindAttribute(3, "in_jointIndices");
				this.bindAttribute(4, "in_weights");
			}
			
			@Override
			public void bindUniformLocations() {
				this.createUniformLocation("proj");
				this.createUniformLocation("view");
				this.createUniformLocation("model");
				for (int i = 0; i < AnimationConstants.MAX_JOINTS; i++) {
					this.createUniformLocation("jointTransforms[" + i + "]");
				}
				this.createUniformLocation("model_type");
				this.createUniformLocation("lightDirection");
				this.createUniformLocation("diffuseMap");
				this.createUniformLocation("specularMap");
				this.createUniformLocation("shadowMap");
				this.createUniformLocation("shadowMapSpace");
				this.createUniformLocation("shadowLength");
				this.createUniformLocation("camPos");
				this.createUniformLocation("specPower");
				this.createUniformLocation("hasSpecular");
			}
		};
		shadowRenderer = new ShadowMapRenderer(camera);

		fbo = new Fbo(Display.getDisplay(), 1280, 720);
		msfbo = new Fbo(Display.getDisplay(), 1280, 720, Fbo.FBO_MULTISAMPLED, 4, 2);
		gbuffer = new GBuffer(1280, 720);
		//GL11.glLineWidth(5);
		PostProcessing.init();
		guirenderer = new GuiRenderer();
	}
	
	public void render(Scene scene) {
		camera.updateMouse();

		shadowRenderer.render(scene, LIGHT_DIR);
		
		//msfbo.bind();
		gbuffer.bind();

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0.11f, 0.11f, 0.11f, 1);
		
		if(scene == null){
			if(gui != null) {
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				guirenderer.render(gui);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glCullFace(GL11.GL_BACK);
			}
			Display.update();
			return;
		}
		shader.start();
		shader.loadMatrix4f("proj", camera.getProjection());
		shader.loadMatrix4f("view", camera.getView());
		shader.loadMatrix4f("shadowMapSpace", shadowRenderer.getToShadowMapSpaceMatrix());
		shader.loadVector("lightDirection", LIGHT_DIR);
		shader.loadVector("camPos", camera.getPosition());
		shader.loadInt("diffuseMap", 0);
		shader.loadInt("shadowMap", 1);
		shader.loadInt("specularMap", 2);
		shader.loadFloat("shadowLength", ShadowBox.SHADOW_DISTANCE);
		shader.loadFloat("specPower", 2);
		
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL.bindTexture(shadowRenderer.getShadowMap());
		
		for (int i = 0; i < scene.getObjects().size(); i++) {
			GameObject object = scene.getObject(i);
			shader.loadMatrix4f("model", object.getModelMatrix());
			object.getTexture().connectToAndBind(0);
			if(object.getSpecular() != null){
				object.getSpecular().connectToAndBind(2);
				shader.loadBoolean("hasSpecular", true);
			}else{
				shader.loadBoolean("hasSpecular", false);
			}
			if(object.getVao().isIgnoreCulling()){
				GL11.glDisable(GL11.GL_CULL_FACE);
			}
			object.getVao().bind();
			
			if(object instanceof AnimatedGameObject){
				shader.loadInt("model_type", TYPE_MODEL_ANIMATED);
				AnimatedGameObject obj = (AnimatedGameObject)object;
				for (int k = 0; k < obj.getJointTransform().length; k++) {
					shader.loadMatrix4f("jointTransforms[" + k + "]", obj.getJointTransform()[k]);
				}
				GL20.glEnableVertexAttribArray(0);
				GL20.glEnableVertexAttribArray(1);
				GL20.glEnableVertexAttribArray(2);
				GL20.glEnableVertexAttribArray(3);
				GL20.glEnableVertexAttribArray(4);
				GL11.glDrawElements(GL11.GL_TRIANGLES, obj.getVao().getIndices());
				GL20.glDisableVertexAttribArray(0);
				GL20.glDisableVertexAttribArray(1);
				GL20.glDisableVertexAttribArray(2);
				GL20.glDisableVertexAttribArray(3);
				GL20.glDisableVertexAttribArray(4);
				obj.getVao().unbind();
			}else{
				shader.loadInt("model_type", TYPE_MODEL_STATIC);
				GL20.glEnableVertexAttribArray(0);
				GL20.glEnableVertexAttribArray(1);
				GL20.glEnableVertexAttribArray(2);
				GL11.glDrawElements(GL11.GL_TRIANGLES, object.getVao().getIndices());
				GL20.glDisableVertexAttribArray(0);
				GL20.glDisableVertexAttribArray(1);
				GL20.glDisableVertexAttribArray(2);
				object.getVao().unbind();
			}
			if(object.getVao().isIgnoreCulling()){
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glCullFace(GL11.GL_BACK);
			}
		}

		shader.stop();
		//msfbo.unbind();
		//msfbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, fbo);
		gbuffer.unbind();
		
		angle += 0.0001f;
		LIGHT_DIR = new Vector3f(angle, -1, 1);
		
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		PostProcessing.doPostProcessing(LIGHT_DIR, camera.getProjection(), gbuffer);
		if(isWireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		}

		if(gui != null) {
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			guirenderer.render(gui);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glCullFace(GL11.GL_BACK);
		}
		
		Display.update();
	}

	public void renderGui(IGui gui) {
		this.gui = gui;
	}

	public void toggleWireframe() {
		if (!isWireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			isWireframe = true;
		} else if (isWireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			isWireframe = false;
		}
	}

	public void destroy() {
		shader.destroy();
		msfbo.destroy();
		fbo.destroy();
	}
	
	public ICamera getCamera() {
		return camera;
	}

	@Override
	public void keyPressed(int key) {
		if(key == GLFW.GLFW_KEY_F1) {
			toggleWireframe();
		}
	}

	@Override
	public void keyReleased(int key) {
		
	}
}