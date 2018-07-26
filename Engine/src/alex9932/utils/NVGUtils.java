package alex9932.utils;

import java.util.HashMap;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;

public class NVGUtils {
	private static HashMap<String, String> fonts = new HashMap<String, String>();
	private static long vg;
	
	static{
		vg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_STENCIL_STROKES);
	}
	
	public static void registerFont(String pathToFont, String name){
		if(vg == 0){throw new IllegalArgumentException("NanoVG not initialized!");}
		if(fonts.get(name) == null){
			fonts.put(name, pathToFont);
			NanoVG.nvgCreateFont(vg, name, pathToFont);
		}else{
			throw new IllegalArgumentException("Font " + name + " already registered!");
		}
	}
	
	public static void drawString(String text, String font, float x, float y, float size){
		if(vg == 0){throw new IllegalArgumentException("NanoVG not initialized!");}
		if(fonts.get(font) != null){
			NanoVG.nvgFontSize(vg, size);
			NanoVG.nvgFontFace(vg, font);
			NanoVG.nvgText(vg, x, y, text);
		}else{
			throw new IllegalArgumentException("Font " + font + " not registered!");
		}
	}
	
	public static NVGColor color(float r, float g, float b, float a) {
		NVGColor color = NVGColor.create();
		NanoVG.nvgRGBAf(r, g, b, a, color);
		return color;
	}
	
	public static long getVg() {
		return vg;
	}

	public static void destroy() {
		NanoVGGL3.nvgDelete(vg);
	}

	public static void drawGlowString(String text, String font, float x, float y, float size, float glow) {
		NanoVG.nvgFontBlur(vg, glow);
		drawString(text, font, x, y, size);
		NanoVG.nvgFontBlur(vg, 0);
		drawString(text, font, x, y, size);
	}
}