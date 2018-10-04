package alex9932.utils;

import org.json.JSONArray;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.ode4j.math.DMatrix3;
import org.ode4j.math.DMatrix3C;
import org.ode4j.math.DVector3C;

import alex9932.engine.physics.Body;

public class MatMath {
	public static Matrix4f createProjectionMatrix(float aspect, float FOV, float NEAR_PLANE, float FAR_PLANE) {
		float aspectRatio = aspect;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		Matrix4f projection = new Matrix4f();
		projection.m00 = x_scale;
		projection.m11 = y_scale;
		projection.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projection.m23 = -1;
		projection.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projection.m33 = 0;
		return projection;
	}
	
	public static Matrix4f createViewMatrix(float x, float y, float z, float anglex, float angley) {
		Matrix4f view = new Matrix4f();
		view.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(angley), new Vector3f(1, 0, 0), view, view);
		Matrix4f.rotate((float) Math.toRadians(anglex), new Vector3f(0, 1, 0), view, view);
		Vector3f negativeCameraPos = new Vector3f(-x, -y, -z);
		Matrix4f.translate(negativeCameraPos, view, view);
		return view;
	}

	public static Matrix4f createRotationMatrix(float anglex, float angley) {
		Matrix4f view = new Matrix4f();
		view.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(angley), new Vector3f(1, 0, 0), view, view);
		Matrix4f.rotate((float) Math.toRadians(anglex), new Vector3f(0, 1, 0), view, view);
		return view;
	}
	
	public static Matrix4f createModelMatrix(float x, float y, float z, float rotx, float roty, float rotz, float scale) {
		Matrix4f model = new Matrix4f();
		model.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(rotx), new Vector3f(1, 0, 0), model, model);
		Matrix4f.rotate((float) Math.toRadians(roty), new Vector3f(0, 1, 0), model, model);
		Matrix4f.rotate((float) Math.toRadians(rotz), new Vector3f(0, 0, 1), model, model);
		Matrix4f.scale(new Vector3f(scale, scale, scale), model, model);
		Vector3f negativeCameraPos = new Vector3f(x, y, z);
		Matrix4f.translate(negativeCameraPos, model, model);
		return model;
	}

	public static Matrix4f createModelMatrixFromBody(Body body) {
		Matrix4f model = new Matrix4f();
		DMatrix3C mat = body.getGeom().getRotation();
		DVector3C pos = body.getGeom().getPosition();
		model.m00 = (float)mat.get00();
		model.m10 = (float)mat.get01();
		model.m20 = (float)mat.get02();
		model.m01 = (float)mat.get10();
		model.m11 = (float)mat.get11();
		model.m21 = (float)mat.get12();
		model.m02 = (float)mat.get20();
		model.m12 = (float)mat.get21();
		model.m22 = (float)mat.get22();
		model.m30 = (float)pos.get0();
		model.m31 = (float)pos.get1();
		model.m32 = (float)pos.get2();
		model.m33 = 1f;
		return model;
	}

	public static Matrix4f createOrtho(float left, float right, float bottom, float top, float near, float far) {
		Matrix4f mat = new Matrix4f();
		mat.m00 = 2 / (right - left);
		mat.m11 = 2 / (top - bottom);
		mat.m22 = 2 / (far - near);
		mat.m30 = -((right + left) / (right - left));
		mat.m31 = -((top + bottom) / (top - bottom));
		mat.m32 = -((far + near) / (far - near));
		mat.m33 = 1;
		
		return mat;
	}

	public static DMatrix3C createDMat3d(JSONArray jsonArray) {
		double a = jsonArray.getDouble(0);
		double b = jsonArray.getDouble(1);
		double c = jsonArray.getDouble(2);
		double d = jsonArray.getDouble(4);
		double e = jsonArray.getDouble(5);
		double f = jsonArray.getDouble(6);
		double g = jsonArray.getDouble(8);
		double h = jsonArray.getDouble(9);
		double i = jsonArray.getDouble(10);
		DMatrix3C mat = new DMatrix3(a, b, c, d, e, f, g, h, i);
		return mat;
	}
}