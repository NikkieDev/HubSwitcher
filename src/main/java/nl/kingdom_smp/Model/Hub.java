package nl.kingdom_smp.Model;

public record Hub (int id, String name, String address, int port) {
	public Hub(int id, String name, String address, int port) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.port = port;
	}

	boolean isConnected(int hubId) {
		return this.id == hubId;
	}

	public int id() {
		return this.id;
	}

	public String address() {
		return this.address;
	}

	public int port() {
		return this.port;
	}
}
