package alex9932.utils.sound;

import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;

public class Listener {
	private Vector3f pos;
	
	public Listener() {
		this(0, 0, 0);
	}
	
	public Listener(float x, float y, float z) {
		this(new Vector3f(x, y, z));
	}
	
	public Listener(Vector3f pos) {
		this.pos = pos;
		setPosition(pos);
		setSpeed(new Vector3f(0, 0, 0));
	}

	public void setSpeed(Vector3f speed) {
		AL10.alListener3f(AL10.AL_VELOCITY, speed.x, speed.y, speed.z);
	}

	public void setPosition(Vector3f position) {
		this.pos.x = position.x;
		this.pos.y = position.y;
		this.pos.z = position.z;
		AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
	}

	public void setOrientation(Vector3f at, Vector3f up) {
		float[] data = new float[6];
		data[0] = at.x;
		data[1] = at.y;
		data[2] = at.z;
		data[3] = up.x;
		data[4] = up.y;
		data[5] = up.z;
		AL10.alListenerfv(AL10.AL_ORIENTATION, data);
	}
	
	public Vector3f getPos() {
		return pos;
	}

	public float getX() {
		return pos.x;
	}
	
	public float getY() {
		return pos.y;
	}
	
	public float getZ() {
		return pos.z;
	}
}