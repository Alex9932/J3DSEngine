package alex9932.utils;

public class Resource {
	public static String GAMEDATA = "gamedata/";
	private static String MODELS_PATH = GAMEDATA + "models/";
	private static String SOUNDS_PATH = GAMEDATA + "sounds/";
	private static String SHADERS_PATH = GAMEDATA + "shaders/";
	private static String SCRIPTS_PATH = GAMEDATA + "scripts/";
	private static String CONFIGS_PATH = GAMEDATA + "configs/";
	private static String TEXTURES_PATH = GAMEDATA + "textures/";
	private static String LEVELS_PATH = GAMEDATA + "levels/";

	public static String getModel(String file) {
		return MODELS_PATH + file;
	}

	public static String getSound(String file) {
		return SOUNDS_PATH + file;
	}

	public static String getScript(String file) {
		return SCRIPTS_PATH + file;
	}

	public static String getConfig(String file) {
		return CONFIGS_PATH + file;
	}

	public static String getShader(String file) {
		return SHADERS_PATH + file;
	}
	
	public static String getTexture(String file) {
		return TEXTURES_PATH + file;
	}

	public static String getLevel(String file) {
		return LEVELS_PATH + file;
	}
}