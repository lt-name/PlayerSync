package net.llamadevelopment.PlayerSync;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import net.llamadevelopment.PlayerSync.listener.PlayerListener;
import net.llamadevelopment.PlayerSync.provider.MongoProvider;
import net.llamadevelopment.PlayerSync.provider.MySQLProvider;
import net.llamadevelopment.PlayerSync.provider.Provider;
import net.llamadevelopment.PlayerSync.utils.Language;
import net.llamadevelopment.PlayerSync.utils.Manager;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class PlayerSync extends PluginBase {

    public static PlayerSync instance;
    public static Provider provider;
    public static Map<String, Provider> providers = new HashMap<>();

    @Override
    public void onLoad() {
        instance = this;
        registerProvider(new MongoProvider());
        registerProvider(new MySQLProvider());
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Config c = getConfig();
        try {
            Language.init();
            Manager.inventory = c.getBoolean("sync.inventory");
            Manager.enderchest = c.getBoolean("sync.enderchest");
            Manager.health = c.getBoolean("sync.health");
            Manager.food = c.getBoolean("sync.food");
            Manager.exp = c.getBoolean("sync.exp");

            String prov = c.getString("provider");
            if (prov.equalsIgnoreCase("ENTER_PROVIDER")) {
                getLogger().info("§a-+-+-+ PlayerSync +-+-+-");
                getLogger().info("§aThanks for downloading PlayerSync! Please choose your provider and fill out the required information in the config.yml file.");
                getLogger().info("§a-+-+-+ PlayerSync +-+-+-");
                return;
            }
            if (!providers.containsKey(prov)) {
                getLogger().info("§c" + prov + " is not a valid provider. Please check if the provider has been correctly installed or check the name defined in config.yml.");
                return;
            }
            provider = providers.get(prov);
            provider.open(getConfig());

            Manager.loadDelay = c.getInt("loadDelay");
            getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(((uuid, player) -> Manager.savePlayer(player)));
    }

    public static PlayerSync getInstance() {
        return instance;
    }

    public void registerProvider(Provider provider) {
        providers.put(provider.getName(), provider);
    }
}
