package nl.kingdom_smp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import nl.kingdom_smp.model.Hub;

public class HubSwitcher extends JavaPlugin {
	private int hubId;
	private List<Hub> hubs;

	@Override
	public void onEnable() {
		getLogger().info("HubSwitcher v0.1 enabled, thank you");
		saveDefaultConfig();

		this.registerHubs();
		
		this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
			event.registrar().register(CommandHub.createCommand(this.hubId, this.hubs));
		});
	}

	private void registerHubs() {
		this.hubId = getConfig().getInt("server.id");
		this.hubs = new ArrayList<>();
		ConfigurationSection servers = getConfig().getConfigurationSection("servers");

		for (String key : servers.getKeys(false)) {
			ConfigurationSection server = servers.getConfigurationSection(key);

			int id = server.getInt("id");
			String name = server.getString("name");
			String rawAddress = server.getString("address");

			String[] parts = rawAddress.split(":");
			String address = parts[0];
			int port = Integer.parseInt(parts[1]);

			Hub hub = new Hub(id, name, address, port);
			this.hubs.add(hub);
		}
	}
}

