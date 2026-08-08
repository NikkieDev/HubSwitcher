package nl.kingdom_smp;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nl.kingdom_smp.Model.Hub;

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
				Material block = activeHubId == hubs.get(i).id() ? Material.BLUE_TERRACOTTA : Material.RED_TERRACOTTA;
				ItemStack item = new ItemStack(block, i+1);
				topInv.setItem(i, item);
			}
			
			view.open();
			return Command.SINGLE_SUCCESS;
		}).build();
	}
}
