package alex9932.script;

import java.io.FileReader;
import java.io.FileWriter;

public class FileIO {
	public static String read(String path) throws Exception {
		StringBuilder str = new StringBuilder();
		FileReader reader = new FileReader(path);
		while (reader.ready()) {
			str.append((char)reader.read());
		}
		reader.close();
		return str.toString();
	}

	public static void write(String path, String text) throws Exception {
		FileWriter writer = new FileWriter(path);
		writer.write(text);
		writer.close();
	}
}