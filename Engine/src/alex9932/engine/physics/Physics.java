package alex9932.engine.physics;

import java.util.ArrayList;
import java.util.List;

import org.ode4j.math.DVector3C;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DContact;
import org.ode4j.ode.DContactBuffer;
import org.ode4j.ode.DGeom;
import org.ode4j.ode.DGeom.DNearCallback;
import org.ode4j.ode.DJoint;
import org.ode4j.ode.DJointGroup;
import org.ode4j.ode.DMass;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.DTriMesh;
import org.ode4j.ode.DTriMesh.DTriArrayCallback;
import org.ode4j.ode.DTriMesh.DTriCallback;
import org.ode4j.ode.DTriMesh.DTriRayCallback;
import org.ode4j.ode.DTriMeshData;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeConfig;
import org.ode4j.ode.OdeConstants;
import org.ode4j.ode.OdeHelper;

public class Physics {
	private DWorld world;
	private DSpace space;
	private List<Body> bodys = new ArrayList<Body>();
	private DJointGroup contactGroup = OdeHelper.createJointGroup();
	private DNearCallback nearCallback = new DNearCallback() {
		@Override
		public void call(Object data, DGeom geom0, DGeom geom1) {
			nearCallBack(data, geom0, geom1);
		}
	};

	public Physics() {
		System.out.println("[ODE] Starting up...");
		OdeHelper.initODE2(0);
		System.out.println("[ODE] Version: " + OdeHelper.getVersion());
		System.out.println("[ODE] Triangle mesh " + (OdeConfig.isTrimeshEnabled() ? "enabled" : "disabled") + "!");
		System.out.println("[ODE] Triangle mesh type " + OdeConfig.dTRIMESH_TYPE + ".");
		System.out.println("[ODE] Double precision " + (OdeConfig.isDoublePrecision() ? "enabled" : "disabled") + "!");
		world = OdeHelper.createWorld();
		space = OdeHelper.createSimpleSpace();
		world.setGravity(0, -9.8, 0);
		world.setQuickStepNumIterations(32);
	}
	
	public void update() {
		final double step = 0.001;
		OdeHelper.spaceCollide(space, 0, nearCallback);
		world.quickStep(step);
		contactGroup.empty();
		
		for (int i = 0; i < bodys.size(); i++) {
			Body body = bodys.get(i);
			DGeom geom = body.getGeom();
			body.update();
			
			if(geom.getPosition().get1() <= -100){
				bodys.remove(geom);
				space.remove(geom);
				geom.destroy();
				geom.DESTRUCTOR();
			}
		}
	}
	
	private void nearCallBack(Object data, DGeom geom0, DGeom geom1) {
		int N = 128;
		int n = 0;

		Body body0 = getBody(geom0);
		Body body1 = getBody(geom1);
		
		double fric0 = body0.getFriction();
		double fric1 = body1.getFriction();
		
		DContactBuffer contacts = new DContactBuffer(N);
		
		n = OdeHelper.collide(geom0, geom1, N, contacts.getGeomBuffer());
		if (n > 0) {
			for (int i = 0; i < n; i++) {
				DContact contact = contacts.get(i);
				
				contact.surface.mode = OdeConstants.dContactApprox0 | OdeConstants.dContactSoftCFM;
				contact.surface.mu = Math.sqrt((fric0 * fric0) + (fric1 * fric1));
				contact.surface.bounce = 0.09;
				contact.surface.bounce_vel = 0.01;
				contact.surface.soft_cfm = 0.001;

				DJoint j = OdeHelper.createContactJoint(world, contactGroup, contact);
				j.attach(contact.geom.g1.getBody(), contact.geom.g2.getBody());
				/**if(getLength(body0.getBody().getLinearVel()) > 7 || getLength(body1.getBody().getLinearVel()) > 7) {
					body0.getSource().setBuffer(body0.getMaterial().getCollideSound());
					body1.getSource().setBuffer(body1.getMaterial().getCollideSound());
					body0.getSource().play();
					body1.getSource().play();
				}**/
			}
		}
	}

	public double getLength(DVector3C vector) {
		return Math.sqrt((vector.get0() * vector.get0()) + (vector.get1() * vector.get1()) + (vector.get2() * vector.get2()));
	}

	public Body getBody(DGeom geom) {
		for (int i = 0; i < bodys.size(); i++) {
			if(bodys.get(i).getGeom().equals(geom)) {
				return bodys.get(i);
			}
		}
		return null;
	}

	public Body createBoxBody(double mass, double friction, double x, double y, double z, double dx, double dy, double dz, Material material) {
		DGeom geom = OdeHelper.createBox(space, dx, dy, dz);
		DBody body = OdeHelper.createBody(world);
		DMass smass = OdeHelper.createMass();
		
		smass.setMass(mass);
		smass.setBox(1, dx, dy, dz);
		body.setMass(smass);
		geom.setBody(body);
		body.setPosition(x, y, z);
		body.setGravityMode(true);
		Body bbody = new Body(material, geom, friction);
		bodys.add(bbody);
		return bbody;
	}
	
	public Body createSphereBody(double mass, double friction, double x, double y, double z, double radius, Material material) {
		DGeom geom = OdeHelper.createSphere(space, radius);
		DBody body = OdeHelper.createBody(world);
		DMass smass = OdeHelper.createMass();
		smass.setMass(mass);
		smass.setSphere(1, radius);
		body.setMass(smass);
		geom.setBody(body);
		body.setPosition(x, y, z);
		body.setGravityMode(true);
		Body bbody = new Body(material, geom, friction);
		bodys.add(bbody);
		return bbody;
	}
	
	public Body createCapsuleBody(double mass, double friction, double x, double y, double z, double radius, double length, Material material) {
		DGeom geom = OdeHelper.createCapsule(space, radius, length);
		DBody body = OdeHelper.createBody(world);
		DMass smass = OdeHelper.createMass();
		smass.setMass(mass);
		smass.setCapsule(material.getDensity(), 2, radius, length);
		body.setMass(smass);
		geom.setBody(body);
		body.setPosition(x, y, z);
		body.setGravityMode(true);
		Body bbody = new Body(material, geom, friction);
		bodys.add(bbody);
		return bbody;
	}

	public Body createTMeshBody(double mass, double friction, double x, double y, double z, float[] vertices, int[] indices, Material material) {
		return createTMeshBody(mass, friction, x, y, z, createMesh(vertices, indices), material);
	}
	
	public 	Body createTMeshBody(double mass, double friction, double x, double y, double z, DTriMeshData data, Material material) {
		DTriMesh geom = OdeHelper.createTriMesh(space, data, new DTriCallback() {
			@Override
			public int call(DGeom arg0, DGeom arg1, int arg2) {
				return 0;
			}
		}, new DTriArrayCallback() {
			@Override
			public void call(DGeom arg0, DGeom arg1, int[] arg2, int arg3) {
			}
		}, new DTriRayCallback() {
			@Override
			public int call(DGeom arg0, DGeom arg1, int arg2, double arg3, double arg4) {
				return 0;
			}
		});
		
		DBody body = OdeHelper.createBody(world);
		DMass smass = OdeHelper.createMass();
		smass.setTrimesh(-mass, geom);
		body.setMass(smass);
		geom.setBody(body);
		body.setPosition(x, y, z);
		body.setGravityMode(true);
		Body bbody = new Body(material, geom, friction);
		bodys.add(bbody);
		return bbody;
	}
	
	public DTriMeshData createMesh(float[] vertices, int[] indices){
		DTriMeshData data = OdeHelper.createTriMeshData();
		data.build(vertices, indices);
		return data;
	}
	
	public void setFixed(DGeom geom) {
		OdeHelper.createFixedJoint(world).attach(geom.getBody(), null);
	}
	
	public DWorld getWorld() {
		return world;
	}
	
	public void destroy() {
		System.out.println("[ODE] Shutting down...");
		contactGroup.destroy();
		space.destroy();
		world.destroy();
		OdeHelper.closeODE();
	}
}