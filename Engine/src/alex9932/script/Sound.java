package alex9932.script;

import alex9932.utils.sound.SoundSystem;
import alex9932.utils.sound.Source;

public class Sound {
	public Sound() {
		
	}
	
	public Source getNewSource(float x, float y, float z, float volume, String path) {
		Source src = SoundSystem.createSource(SoundSystem.getSoundBuffer(path), x, y, z);
		src.setVolume(volume);
		return src;
	}
}