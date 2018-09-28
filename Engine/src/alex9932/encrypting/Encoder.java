package alex9932.encrypting;

public class Encoder {
	public static void encode(byte[] data) {
		byte[] ba = {(byte)'H', (byte)'e', (byte)'l', (byte)'l', (byte)'0', (byte)'!'};
		byte[] ba1 = new byte[ba.length];;
		
		IEMagic magic = new IEMagic();
		int pos = magic.getPos();
		for (int i = 0; i < ba.length; i++) {
			ba1[i] = magic.get(ba[i]);
		}
		
		magic.setPos(pos);
		for (int i = 0; i < ba.length; i++) {
			ba[i] = magic.get(ba1[i]);
		}

		System.out.println(new String(ba));
		System.out.println(new String(ba1));
	}
}