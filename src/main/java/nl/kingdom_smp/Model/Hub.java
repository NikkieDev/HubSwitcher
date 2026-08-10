package nl.kingdom_smp.model;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import nl.kingdom_smp.Networking.Connection;
import nl.kingdom_smp.Networking.Util;

public record Hub (int id, String name, String address, int port) {
	public boolean isConnected(int hubId) {
		return this.id == hubId;
	}

	public byte[] handshake() {
		Connection conn;
		try {
			conn = new Connection(address, port);
		} catch (IOException e) {
			return new byte[0];
		}

		byte[] host = address.getBytes(StandardCharsets.UTF_8);
		byte[] packetType = Util.encodeVarInt(0x00);
		byte[] version = Util.encodeVarInt(-0x01);
		byte[] hostLength = Util.encodeVarInt(host.length);
		byte[] nextState = Util.encodeVarInt(0x01);

		ByteBuffer buf = ByteBuffer.allocate(
			host.length
			+ packetType.length
			+ version.length
			+ hostLength.length
			+ 2 // Short int
			+ nextState.length
		);

		buf.put(packetType);
		buf.put(version);
		buf.put(hostLength);
		buf.put(host);
		buf.putShort((short) port);
		buf.put(nextState);

		byte[] statusRequest = Util.encodeVarInt(0x00);
		byte[] pingRequest = Util.encodeVarInt(0x01);

		try {
			conn.write(Util.encodeVarInt(buf.capacity()));
			conn.write(buf.array());

			conn.write(Util.encodeVarInt(statusRequest.length));
			conn.write(statusRequest);

			int responseLength = conn.readVarInt();
			conn.read(responseLength);

			ByteBuffer buf2 = ByteBuffer.allocate(9);
			buf2.put(pingRequest);
			buf2.putLong(12345689L);

			conn.write(Util.encodeVarInt(buf2.capacity()));
			conn.write(buf2.array());

			responseLength = conn.readVarInt();
			return conn.read(responseLength);
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}
}
