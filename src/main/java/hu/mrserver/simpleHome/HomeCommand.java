package hu.mrserver.simpleHome;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class HomeCommand implements CommandExecutor {

    private final SimpleHome plugin;
    private final HashMap<UUID, Location> teleporting = new HashMap<>();


    public HomeCommand(SimpleHome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));

        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.getString("not-player").replace("&", "§"));
            return true;
        }

        Location home = plugin.getHomes().get(player.getUniqueId());
        if (home == null) {
            player.sendMessage(messages.getString("no-home").replace("&", "§"));
            return true;
        }

        // világ ellenőrzés
        if (!plugin.getConfig().getBoolean("teleport-over-the-worlds") && !home.getWorld().equals(player.getWorld())) {
            player.sendMessage(messages.getString("teleport-wrong-world").replace("&", "§"));
            return true;
        }

        // ha már teleportálás folyamatban van
        if (teleporting.containsKey(player.getUniqueId())) {
            player.sendMessage(messages.getString("already-teleporting").replace("&", "§"));
            return true;
        }

        int cost = plugin.getConfig().getInt("teleport-cost");
        // XP szint ellenőrzés
        if (player.getLevel() < cost) {
            player.sendMessage(messages.getString("not-enough-xp-for-teleport").replace("{cost}", String.valueOf(cost)).replace("&", "§"));
            return true;
        }

        // ha van elég, akkor levonjuk
        player.setLevel(player.getLevel() - cost);

        Location startLoc = player.getLocation();
        teleporting.put(player.getUniqueId(), startLoc);

        int seconds = plugin.getConfig().getInt("teleport-delay");

        // kezdő ActionBar üzenet a configból
        String startMsg = messages.getString("teleport-start")
                .replace("{delay}", String.valueOf(seconds))
                .replace("&", "§");
        player.sendActionBar(startMsg);

        new BukkitRunnable() {
            int countdown = seconds; // belső változó a visszaszámláláshoz

            @Override
            public void run() {
                if (!teleporting.containsKey(player.getUniqueId())) {
                    cancel();
                    return;
                }

                Location current = player.getLocation();

                // Ellenőrzés: ha mozgott (0 tűréshatár)
                if (current.getX() != startLoc.getX() ||
                        current.getY() != startLoc.getY() ||
                        current.getZ() != startLoc.getZ()) {
                    teleporting.remove(player.getUniqueId());
                    player.sendActionBar(messages.getString("teleport-cancelled").replace("&", "§"));
                    player.setLevel(player.getLevel() + cost); // XP visszaadása
                    cancel();
                    return;
                }

                if (countdown > 0) {
                    String tickMsg = messages.getString("teleport-start")
                            .replace("{delay}", String.valueOf(countdown))
                            .replace("&", "§");
                    player.sendActionBar(tickMsg);
                    countdown--;
                } else {
                    player.teleport(home);
                    player.sendMessage(messages.getString("teleport-success").replace("&", "§"));
                    teleporting.remove(player.getUniqueId());
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // minden másodpercben fut

        return true;
    }
}
