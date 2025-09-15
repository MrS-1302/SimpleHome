package hu.mrserver.simpleHome;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class DelHomeCommand implements CommandExecutor {

    private final SimpleHome plugin;

    public DelHomeCommand(SimpleHome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));

        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.getString("not-player").replace("&", "§"));
            return true;
        }

        if (plugin.getHomes().containsKey(player.getUniqueId())) {
            plugin.getHomes().remove(player.getUniqueId());
            plugin.saveHomes(); // Változtatás mentése
            player.sendMessage(messages.getString("home-deleted").replace("&", "§"));
        } else {
            player.sendMessage(messages.getString("no-home").replace("&", "§"));
        }
        return true;
    }
}
