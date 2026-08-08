package nl.kingdom_smp.model;

public record Hub (int id, String name, String address, int port) {
	public boolean isConnected(int hubId) {
		return this.id == hubId;
	}
}
