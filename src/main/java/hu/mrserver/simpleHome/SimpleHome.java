package hu.mrserver.simpleHome;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public final class SimpleHome extends JavaPlugin {

    private final HashMap<UUID, Location> homes = new HashMap<>();
    private File homesFile;
    private YamlConfiguration homesConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);

        homesFile = new File(getDataFolder(), "homes.yml");
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);

        loadHomes();

        // Parancsok regisztrálása
        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("delhome").setExecutor(new DelHomeCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("simplehome").setExecutor(new SimpleHomeCommand(this));

        getLogger().info("SimpleHome plugin bekapcsolva!");
    }

    @Override
    public void onDisable() {
        saveHomes();
        getLogger().info("SimpleHome plugin kikapcsolva!");
    }

    public HashMap<UUID, Location> getHomes() {
        return homes;
    }

    public void saveHomes() {
        for (UUID uuid : homes.keySet()) {
            homesConfig.set(uuid.toString(), homes.get(uuid));
        }
        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadHomes() {
        if (homesFile.exists()) {
            for (String key : homesConfig.getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                Location loc = homesConfig.getLocation(key);
                if (loc != null) {
                    homes.put(uuid, loc);
                }
            }
        }
    }
}
