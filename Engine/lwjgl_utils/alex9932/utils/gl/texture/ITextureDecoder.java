package alex9932.utils.gl.texture;

public interface ITextureDecoder {
	TextureData decode(String path) throws Exception;
}