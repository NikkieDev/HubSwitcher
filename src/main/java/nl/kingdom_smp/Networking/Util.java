package nl.kingdom_smp.Networking;

import java.util.Arrays;

public class Util {
	public static byte[] encodeVarInt(int value) {
		byte[] bytes = new byte[5];
		int index = 0;

		while (true) {
			int part = value & 0x7F; // Take 7 LEAST significat bits from byte
			value >>>= 7;
			if (0 != value) {
				bytes[index++] = (byte) (part | 0x80);
			} else {
				bytes[index++] = (byte) part;
				break;
			}
		}

		return Arrays.copyOf(bytes, index);
	}

	public record DecodedVarInt (int result, int read) {}
	public static DecodedVarInt decodeVarInt(byte[] data, int offset) {
		int result = 0;
		int numRead = 0;
		byte part;

		do {
			part = data[offset + numRead];
			result |= (part & 0x7F) << (7 * numRead);
			numRead++;
		} while ((part & 0x80) != 0);

		return new DecodedVarInt(result, numRead);
	}
}
