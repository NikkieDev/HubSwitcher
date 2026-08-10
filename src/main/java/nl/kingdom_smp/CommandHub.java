package nl.kingdom_smp;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nl.kingdom_smp.model.Hub;

public class CommandHub {
	public static LiteralCommandNode<CommandSourceStack> createCommand(int activeHubId, List<Hub> hubs) {
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
			for (int i = 0; i < hubs.size(); i++) {
				Material block = hubs.get(i).isConnected(activeHubId) ? Material.BLUE_TERRACOTTA : Material.RED_TERRACOTTA;
				ItemStack item = new ItemStack(block, i+1);
				configureItem(item, hubs.get(i));

				topInv.setItem(i, item);
			}
			
			view.open();
			return Command.SINGLE_SUCCESS;
		}).build();
	}

	// Set hub as item metadata
	private static void configureItem(ItemStack item, Hub hub) {
		ItemMeta meta = item.getItemMeta();
		List<Component> loreList = new ArrayList<Component>();

		String status = 0 < hub.ping() ? "Online" : "Offline";
		loreList.add(Component.text("Status: " + status));

		meta.customName(Component.text("Hub#" + hub.id()));
		meta.lore(loreList);
		item.setItemMeta(meta);
	}
}
