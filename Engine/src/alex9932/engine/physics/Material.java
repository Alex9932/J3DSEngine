package alex9932.engine.physics;

import java.util.Random;

import alex9932.utils.Resource;
import alex9932.utils.sound.Buffer;
import alex9932.utils.sound.SoundSystem;

public class Material {
	private static final Random random = new Random();

	public static Material PLAYER = new Material(1.874, new String[]{"human/collide/clothes1.ogg", "human/collide/clothes2.ogg", "human/collide/clothes3.ogg"});
	public static Material METAL = new Material(7.874, new String[]{"barrel/collide/barrel_1.ogg", "barrel/collide/barrel_2.ogg"});
	
	private double density;
	private Buffer[] sounds;
	
	public Material(double density, String[] collidesounds) {
		this.density = density;
		this.sounds = new Buffer[collidesounds.length];
		for (int i = 0; i < collidesounds.length; i++) {
			this.sounds[i] = SoundSystem.getSoundBuffer(Resource.getSound("material/" + collidesounds[i]));
		}
	}
	
	public Buffer getCollideSound() {
		return sounds[random.nextInt(sounds.length)];
	}
	
	public double getDensity() {
		return density;
	}
}