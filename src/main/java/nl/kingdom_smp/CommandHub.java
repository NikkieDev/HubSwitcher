package nl.kingdom_smp;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nl.kingdom_smp.model.Hub;

public class CommandHub implements Listener{
	private final NamespacedKey hubIdKey;
	private final List<Hub> hubs;

	public CommandHub(JavaPlugin plugin, List<Hub> hubs) {
		this.hubs = hubs;
		this.hubIdKey = new NamespacedKey(plugin, "hub-id");
	}

	public LiteralCommandNode<CommandSourceStack> createCommand(int activeHubId) {
		return Commands.literal("hub").executes(ctx -> {
			if (!(ctx.getSource().getExecutor() instanceof Player player)) {
				return 0;
			}

			InventoryView view = MenuType.GENERIC_3X3.builder()
				.title(Component.text("Switch hub", NamedTextColor.BLACK))
				.build(player)
			;

			// Get's the chest inventory. getBottomInventory is the player's inventory
			Inventory topInv = view.getTopInventory();
			for (int i = 0; i < this.hubs.size(); i++) {
				Material block = this.hubs.get(i).isConnected(activeHubId) ? Material.BLUE_TERRACOTTA : Material.RED_TERRACOTTA;
				ItemStack item = new ItemStack(block, i+1);
				this.configureItem(item, i);

				topInv.setItem(i, item);
			}
			
			view.open();
			return Command.SINGLE_SUCCESS;
		}).build();
	}

	// Set hub as item metadata
	private void configureItem(ItemStack item, int hubIndex) {
		Hub hub = this.hubs.get(hubIndex);

		List<Component> loreList = new ArrayList<Component>();
		ItemMeta meta = item.getItemMeta();
		meta.getPersistentDataContainer().set(this.hubIdKey, PersistentDataType.INTEGER, hub.id());

		String status = 0 < hub.ping() ? "Online" : "Offline";
		loreList.add(Component.text("Status: " + status));

		meta.customName(Component.text("Hub#" + hub.id()));
		meta.lore(loreList);
		item.setItemMeta(meta);
	}

	@EventHandler
	private void onServerPress(InventoryClickEvent event) {
		if (null == event.getClickedInventory()
			|| !event.getClickedInventory().equals(event.getView().getTopInventory())
		) {
			event.setCancelled(true);
			return;
		}

		ItemStack clicked = event.getCurrentItem();
		if (null == clicked || !clicked.hasItemMeta()) {
			event.setCancelled(true);
			return;
		}

		Integer hubId = clicked.getPersistentDataContainer().get(this.hubIdKey, PersistentDataType.INTEGER);
		if (null == hubId) {
			event.setCancelled(true);
			return;
		}

		Hub target = this.hubs.stream().filter(h -> h.id() == hubId).findFirst().orElse(null);

		if (null == target || !(event.getWhoClicked() instanceof Player player)) {
			event.setCancelled(true);
			return;
		}

		player.transfer(target.address(), target.port());
	}
}
