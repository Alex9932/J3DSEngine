package alex9932.script;

import java.io.File;

import org.json.JSONObject;

public class Json {
	private static final String CONFIG_PATH = "gamedata/config/";

	public Json() {
		
	}
	
	public JSONObject loadFromFile(String path) throws Exception {
		String fullPath = CONFIG_PATH + path;
		if(new File(fullPath).exists()){
			return new JSONObject(FileIO.read(fullPath));
		}else{
			return null;
		}
	}
}