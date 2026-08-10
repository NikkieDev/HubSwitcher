package nl.kingdom_smp.Networking;

import java.io.IOException;
import java.net.Socket;

public class Connection {
	private static final int TIMEOUT_MS = 3000;

	private Socket socket;

	public Connection(String address, int port) throws IOException {
		this.socket = new Socket(address, port);
		this.socket.setSoTimeout(TIMEOUT_MS);
	}

	public void close() {
		try {
			this.socket.close();
			this.socket = null;
		} catch (IOException e) {}
	}

	/*
	 * <summary>
	 * Write byte[] data to connected socket
	 * </summary>
	 *
	 */
	public void write(byte[] data) throws IOException {
		this.socket.getOutputStream().write(data);
	}

	public byte[] read(int size) throws IOException {
		return this.socket.getInputStream().readNBytes(size);
	}

	public byte[] read() throws IOException {
		return this.read(1024);
	}

	public int readVarInt() throws IOException {
		int result = 0;

		for (int numRead = 0; numRead < 5; numRead++) {
			byte[] single = this.read(1);
			if (single.length == 0) {
				throw new IOException("Connection closed while reading VarInt");
			}

			byte part = single[0];
			result |= (part & 0x7F) << (7 * numRead);
			if ((part & 0x80) == 0) {
				return result;
			}
		}

		throw new IOException("VarInt is too long");
	}
}
