package hu.mrserver.simpleHome;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public class SetHomeCommand implements CommandExecutor {

    private final SimpleHome plugin;

    public SetHomeCommand(SimpleHome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));

        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.getString("not-player").replace("&", "§"));
            return true;
        }

        Location loc = player.getLocation();
        World world = loc.getWorld();
        List<String> allowedWorlds = plugin.getConfig().getStringList("allowed-worlds");
        if (!allowedWorlds.contains(world.getName())) {
            player.sendMessage(messages.getString("not-allowed-in-this-world").replace("&", "§"));
            return true;
        }

        int cost = plugin.getConfig().getInt("sethome-cost");
        if (player.getLevel() < cost) {
            player.sendMessage(messages.getString("not-enough-xp-for-set-home").replace("{cost}", String.valueOf(cost)).replace("&", "§"));
            return true;
        }

        player.setLevel(player.getLevel() - cost);

        if (plugin.getHomes().containsKey(player.getUniqueId())) {
            player.sendMessage(messages.getString("home-updated").replace("&", "§"));
        } else {
            player.sendMessage(messages.getString("home-set").replace("&", "§"));
        }
        plugin.getHomes().put(player.getUniqueId(), loc);
        plugin.saveHomes(); // Változtatás mentése

        return true;
    }
}
