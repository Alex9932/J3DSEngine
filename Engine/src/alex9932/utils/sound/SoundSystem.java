package alex9932.utils.sound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vector.Vector3f;

public class SoundSystem {
	private static long device = 0;
	private static long context;
	private static final HashMap<String, Buffer> soundBufferMap = new HashMap<String, Buffer>();
	
	private static Listener listener;
	private static ALCCapabilities deviceCaps;
	private static ALCapabilities caps;
	
	public static void init() {
		device = ALC10.alcOpenDevice((ByteBuffer) null);
		if (device == MemoryUtil.NULL) {
			throw new IllegalStateException("Failed to open the default OpenAL device.");
		}
		
		deviceCaps = ALC.createCapabilities(device);
		context = ALC10.alcCreateContext(device, (IntBuffer) null);
		if (context == MemoryUtil.NULL) {
			throw new IllegalStateException("Failed to create OpenAL context.");
		}
		ALC10.alcMakeContextCurrent(context);
		AL.createCapabilities(deviceCaps);
		caps = AL.getCapabilities();
		AL.setCurrentProcess(SoundSystem.getCaps());
		
		listener = new Listener();
	}
	
	public static Buffer getSoundBuffer(String path){
		if(soundBufferMap.get(path) == null){
			try {
				Buffer buffer = new Buffer(path);
				soundBufferMap.put(path, buffer);
				return buffer;
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Unable to load sound from: " + path + "!");
			}
		}else{
			return soundBufferMap.get(path);
		}	
	}
	
	public static ALCapabilities getCaps() {
		return caps;
	}
	
	public static long getContext() {
		return context;
	}
	
	public static Source createSource(Buffer buffer, float x, float y, float z) {
		return new Source(buffer, x, y, z);
	}
	
	public static Listener getListener() {
		return listener;
	}
	
	public static void updateListenerPosition(float x, float y, float z, float andlex, float angley) {
		Vector3f at = new Vector3f();
		Vector3f up = new Vector3f(0, 1, 0);
		
		at.x = (float)-Math.sin(Math.toRadians(-andlex));
		at.y = (float)-Math.tan(Math.toRadians(angley));
		at.z = (float)-Math.cos(Math.toRadians(-andlex));
		at.normalise();

		listener.setPosition(new Vector3f(x, y, z));
		listener.setOrientation(at, up);
	}
	
	public static void cleanUp() {
		try{
			ALC10.alcCloseDevice(device);
		}catch (Exception e) {
			System.out.println("Close device failed!");
		}
		ALC.destroy();
	}

	public static void setAttenuationModel(int atten) {
		AL10.alDistanceModel(atten);
	}
	
	public static ALCCapabilities getDeviceCaps() {
		return deviceCaps;
	}
}