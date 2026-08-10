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

	public int ping() {
		Connection conn;

		try {
			conn = new Connection(this.address, this.port);
			this.handshake(conn);
		} catch (IOException e) {
			return -1;
		}

		long timeBefore = System.currentTimeMillis();
		ByteBuffer packet = ByteBuffer.allocate(9);
		packet.put(Util.encodeVarInt(0x01));
		packet.putLong(timeBefore);

		try {
			conn.write(Util.encodeVarInt(packet.capacity()));
			conn.write(packet.array());

			int responseLength = conn.readVarInt();
			conn.read(responseLength);
		} catch (IOException e) {
			conn.close();
			return -1;
		}

		return (int) (System.currentTimeMillis() - timeBefore);
	}

	private void handshake(Connection conn) throws IOException {
		byte[] host = this.address.getBytes(StandardCharsets.UTF_8);
		byte[] packetId = Util.encodeVarInt(0x00);
		byte[] version = Util.encodeVarInt(-0x01);
		byte[] hostLength = Util.encodeVarInt(host.length);
		byte[] nextState = Util.encodeVarInt(0x01);

		ByteBuffer packet = ByteBuffer.allocate(
			host.length
			+ packetId.length
			+ version.length
			+ hostLength.length
			+ 2 // port, short int
			+ nextState.length
		);

		packet.put(packetId);
		packet.put(version);
		packet.put(hostLength);
		packet.put(host);
		packet.putShort((short) this.port);
		packet.put(nextState);

		conn.write(Util.encodeVarInt(packet.capacity()));
		conn.write(packet.array());
		conn.write(Util.encodeVarInt(packetId.length));
		conn.write(packetId);
	}
}
