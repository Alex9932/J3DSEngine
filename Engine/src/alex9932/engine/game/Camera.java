package alex9932.engine.game;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import alex9932.engine.render.ICamera;
import alex9932.utils.EventSystem;
import alex9932.utils.MatMath;

public class Camera implements ICamera {
	private float x, y, z;
	private float anglex, angley;
	private float near, far, fov;
	private Matrix4f projection;
	private Matrix4f view;
	private float SENS = 0.1f;
	private float SPEED = 0.01f;
	
	public Camera(float fov, float near, float far, float x, float y, float z) {
		this.near = near;
		this.far = far;
		this.fov = fov;
		this.x = x;
		this.y = y;
		this.z = z;
		this.projection = MatMath.createProjectionMatrix((float) Display.getWidth() / (float) Display.getHeight(), fov, near, far);
		this.view = MatMath.createViewMatrix(x, y, z, anglex, angley);
	}
	
	public void setSens(float SENS) {
		this.SENS = SENS;
	}
	
	public void setSpeed(float SPEED) {
		this.SPEED = SPEED;
	}

	@Override
	public void updateMouse() {
		EventSystem system = Display.getDisplay().getEventSystem();
		this.anglex -= system.getMouseDX() * SENS;
		this.angley -= system.getMouseDY() * SENS;
		if (anglex > 360) {
			anglex = 0;
		}
		if (anglex < 0) {
			anglex = 360;
		}
	}
	
	@Override
	public void update() {
		EventSystem system = Display.getDisplay().getEventSystem();

		Vector3f dpos = new Vector3f();
		Vector3f forward = new Vector3f();
		forward.x = (float)Math.sin(Math.toRadians(-anglex));
		forward.y = (float)Math.tan(Math.toRadians(angley));
		forward.z = (float)Math.cos(Math.toRadians(-anglex));

		//forward = new Vector3f(Matrix4f.transform(MatMath.createRotationMatrix(anglex, angley), new Vector4f(1, 0, 0, 1), null));
		
		if(system.isKeyDown(GLFW.GLFW_KEY_W)){
			Vector3f.sub(dpos, forward, dpos);
		}
		
		if(system.isKeyDown(GLFW.GLFW_KEY_S)){
			Vector3f.add(dpos, forward, dpos);
		}
		
		if(system.isKeyDown(GLFW.GLFW_KEY_A)){
			Vector3f.add(dpos, Vector3f.cross(forward, new Vector3f(0, 1, 0), null), dpos);
		}
		
		if(system.isKeyDown(GLFW.GLFW_KEY_D)){
			Vector3f.sub(dpos, Vector3f.cross(forward, new Vector3f(0, 1, 0), null), dpos);
		}

		try {dpos.normalise();} catch (Exception e) {}
		
		this.x += dpos.x * SPEED;
		this.y += dpos.y * SPEED;
		this.z += dpos.z * SPEED;
		
		this.view = MatMath.createViewMatrix(x, y, z, anglex, angley);
	}

	@Override
	public Matrix4f getProjection() {
		return projection;
	}

	@Override
	public Matrix4f getView() {
		return view;
	}
	
	@Override
	public float getX() {
		return x;
	}
	
	@Override
	public float getY() {
		return y;
	}
	
	@Override
	public float getZ() {
		return z;
	}

	@Override
	public Vector3f getPosition() {
		return new Vector3f(x, y, z);
	}

	@Override
	public float getNear() {
		return near;
	}

	@Override
	public float getFar() {
		return far;
	}

	@Override
	public float getFov() {
		return fov;
	}

	@Override
	public float getAngleX() {
		return anglex;
	}

	@Override
	public float getAngleY() {
		return angley;
	}

	@Override
	public void setPositionAXAY(double x, double y, double z, double ax, double ay) {
		this.x = (float)x;
		this.y = (float)y;
		this.z = (float)z;
		this.anglex = (float)ax;
		this.angley = (float)ay;
	}
}