package alex9932.engine.physics;

import org.ode4j.ode.DBody;
import org.ode4j.ode.DGeom;

import alex9932.utils.sound.SoundSystem;
import alex9932.utils.sound.Source;

public class Body {
	private DGeom geom;
	private DBody body;
	private Material material;
	private Source source;
	private double friction;
	
	public Body(Material material, DGeom geom, double friction) {
		this.geom = geom;
		this.material = material;
		this.friction = friction;
		this.body = geom.getBody();
		this.source = SoundSystem.createSource(null, (float)geom.getPosition().get0(), (float)geom.getPosition().get1(), (float)geom.getPosition().get2());
	}
	
	public void update() {
		this.source.setPosition((float)geom.getPosition().get0(), (float)geom.getPosition().get1(), (float)geom.getPosition().get2());
	}
	
	public DBody getBody() {
		return body;
	}
	
	public DGeom getGeom() {
		return geom;
	}
	
	public double getFriction() {
		return friction;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public Source getSource() {
		return source;
	}
}