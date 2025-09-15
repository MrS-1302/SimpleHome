package hu.mrserver.simpleHome;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class SimpleHomeCommand implements CommandExecutor {

    private final SimpleHome plugin;

    public SimpleHomeCommand(SimpleHome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));

        if (args.length == 0) {
            sender.sendMessage(messages.getString("how-to-usage").replace("&", "§").replace("{command}", "simplehome reload"));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("simplehome.admin")) {
                sender.sendMessage(messages.getString("no-permission").replace("&", "§"));
                return true;
            }
            plugin.reloadConfig();
            sender.sendMessage(messages.getString("plugin-reloaded").replace("&", "§"));
            return true;
        }

        sender.sendMessage(messages.getString("unknown-command").replace("&", "§"));
        sender.sendMessage(messages.getString("how-to-usage").replace("&", "§").replace("{command}", "simplehome reload"));
        return true;
    }
}
