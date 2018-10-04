package alex9932.utils.gl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public abstract class Shader {
	private int program;
	private int vs;
	private int ps;
	private HashMap<String, Integer> uniforms = new HashMap<String, Integer>();
	
	public Shader(String vs, String ps) {
		this.program = GL20.glCreateProgram();
		
		this.vs = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		GL20.glShaderSource(this.vs, readFile(vs));
		GL20.glCompileShader(this.vs);
		checkError("vertex shader", this.vs);
		
		this.ps = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		GL20.glShaderSource(this.ps, readFile(ps));
		GL20.glCompileShader(this.ps);
		checkError("pixel shader", this.ps);

		GL20.glAttachShader(this.program, this.vs);
		GL20.glAttachShader(this.program, this.ps);
		
		bindAttribs();
		
		GL20.glLinkProgram(this.program);
		if(GL20.glGetProgrami(this.program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE){
			System.err.println("[Shader] Link error!");
			System.out.println(GL20.glGetShaderInfoLog(this.program, 500));
			System.exit(1);
		}
		GL20.glValidateProgram(this.program);
		if(GL20.glGetProgrami(this.program, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE){
			System.err.println("[Shader] Validate error!");
			System.out.println(GL20.glGetShaderInfoLog(this.program, 500));
			System.exit(1);
		}

		bindUniformLocations();
	}
	
	public void createUniformLocation(String uniformName){
		uniforms.put(uniformName, GL20.glGetUniformLocation(this.program, uniformName));
	}
	
	public void bindFragOutput(int attachment, String uniformName){
		GL30.glBindFragDataLocation(program, attachment, uniformName);
	}
	
	public void loadBoolean(String uniformName, boolean value){
		float toLoad = 0;
		if(value){
			toLoad = 1;
		}
		GL20.glUniform1f(uniforms.get(uniformName), toLoad);
	}
	
	public void loadFloat(String uniformName, float value){
		GL20.glUniform1f(uniforms.get(uniformName), value);
	}
	
	public void loadInt(String uniformName, int value){
		GL20.glUniform1i(uniforms.get(uniformName), value);
	}
	
	public void loadVector(String uniformName, Vector2f vector){
		GL20.glUniform2f(uniforms.get(uniformName), vector.x, vector.y);
	}
	
	public void loadVector(String uniformName, Vector3f vector){
		GL20.glUniform3f(uniforms.get(uniformName), vector.x, vector.y, vector.z);
	}
	
	public void loadVector(String uniformName, Quaternion vector){
		GL20.glUniform4f(uniforms.get(uniformName), vector.x, vector.y, vector.z, vector.w);
	}
	
	public void loadMatrix4f(String uniformName, Matrix4f matrix){
		if(matrix == null){
			return;
		}
		GL20.glUniformMatrix4fv(uniforms.get(uniformName), false, matrix.getGLMatrix());
	}
	
	public abstract void bindAttribs();
	public abstract void bindUniformLocations();
	
	public void bindAttribute(int id, String name) {
		GL20.glBindAttribLocation(this.program, id, name);
	}
	
	public void start() {
		GL20.glUseProgram(this.program);
	}
	
	public void stop() {
		GL20.glUseProgram(0);
	}

	public void destroy() {
		GL20.glUseProgram(0);
		GL20.glDeleteShader(vs);
		GL20.glDeleteShader(ps);
		GL20.glDeleteProgram(program);
	}

	private void checkError(String name, int shader) {
		if(GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE){
			System.err.println("[Shader] Error in " + name + "!");
			System.out.println("[Shader] " + GL20.glGetShaderInfoLog(shader, 500));
			System.err.println("[Shader] Could not compile shader!");
			System.exit(1);
		}
	}

	public String readFile(String file){
		try{
			System.out.println("[Shader] Reading: " + file);
			String s = "";
			InputStream stream = new FileInputStream(new File(file));
			
			int data = 0;
			while ((data = stream.read()) != -1) {
				s += (char)data;
			}
			
			stream.close();
			return s;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}