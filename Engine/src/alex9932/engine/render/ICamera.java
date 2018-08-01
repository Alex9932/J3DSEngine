package alex9932.engine.render;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public interface ICamera {
	public Matrix4f getProjection();
	public Matrix4f getView();
	public Vector3f getPosition();
	public float getX();
	public float getY();
	public float getZ();
	public float getAngleX();
	public float getAngleY();
	public float getNear();
	public float getFar();
	public float getFov();
	public void update();
	public void updateMouse();
	public void setPositionAXAY(double x, double y, double z, double ax, double ay);
}