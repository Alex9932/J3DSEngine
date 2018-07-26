package alex9932.utils.sound;

import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;

public class Source {
	private float x, y, z;
	private Buffer buffer;
	private int sourceId;

	public Source(Buffer buffer, float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.sourceId = AL10.alGenSources();
		if (buffer != null) {
			this.setBuffer(buffer);
		}
	}

	public void setBuffer(Buffer buffer) {
		this.buffer = buffer;
		AL10.alSourcei(sourceId, AL10.AL_BUFFER, this.buffer.getBufferId());
		setPosition(x, y, z);
		setVolume(1f);
		AL10.alSourcef(sourceId, AL10.AL_ROLLOFF_FACTOR, 1.5f);
	}

	public void setVolume(float gain) {
		AL10.alSourcef(sourceId, AL10.AL_GAIN, gain);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public void setX(float x) {
		this.x = x;
		AL10.alSource3f(sourceId, AL10.AL_POSITION, x, this.y, this.z);
	}

	public void setY(float y) {
		this.y = y;
		AL10.alSource3f(sourceId, AL10.AL_POSITION, this.x, y, this.z);
	}

	public void setZ(float z) {
		this.z = z;
		AL10.alSource3f(sourceId, AL10.AL_POSITION, this.x, this.y, z);
	}

	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		AL10.alSource3f(sourceId, AL10.AL_POSITION, x, y, z);
	}

	public void setPosition(Vector3f position) {
		this.setPosition(position.x, position.y, position.z);
	}

	public void setSpeed(float x, float y, float z) {
		AL10.alSource3f(sourceId, AL10.AL_VELOCITY, x, y, z);
	}

	public void play() {
		AL10.alSourcePlay(sourceId);
	}

	public boolean isPlaying() {
		return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	public void pause() {
		AL10.alSourcePause(sourceId);
	}

	public void stop() {
		AL10.alSourceStop(sourceId);
	}

	public void repeat(boolean repeat) {
		AL10.alSourcei(sourceId, AL10.AL_LOOPING, repeat ? AL10.AL_TRUE
				: AL10.AL_FALSE);
	}

	public void cleanup() {
		stop();
		AL10.alDeleteSources(sourceId);
	}
}