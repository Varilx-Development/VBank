package de.varilx.vaxbank;

import de.varilx.BaseAPI;
import de.varilx.command.registry.VaxCommandRegistry;
import de.varilx.database.Service;
import de.varilx.vaxbank.commands.BankCommand;
import de.varilx.vaxbank.listener.ConnectionListener;
import de.varilx.vaxbank.user.BankUser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class VBank extends JavaPlugin {

    Service databaseService;
    Economy economy;

    @Override
    public void onEnable() {
        new BaseAPI(this).enable();
        initializeDatabaseService();
        if (!setupEconomy()) {
            getLogger().severe("Vault could not be found! Please install Vault and an Economy plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
    }

    private void registerCommands() {
        VaxCommandRegistry commandRegistry = new VaxCommandRegistry();
        commandRegistry.registerCommand(new BankCommand(this));
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ConnectionListener(this), this);
    }

    private void initializeDatabaseService() {
        databaseService = Service.load(BaseAPI.getBaseAPI().getDatabaseConfiguration().getConfig(), getClassLoader());
        databaseService.create(BankUser.class, UUID.class);
    }


    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return (economy != null);
    }

}
