package de.varilx.vaxbank;

import de.varilx.BaseAPI;
import de.varilx.database.Service;
import de.varilx.vaxbank.user.BankUser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class VBank extends JavaPlugin {

    Service databaseService;

    @Override
    public void onEnable() {
        new BaseAPI(this).enable();
        initializeDatabaseService();
    }

    @Override
    public void onDisable() {
    }

    private void initializeDatabaseService() {
        databaseService = Service.load(BaseAPI.getBaseAPI().getDatabaseConfiguration().getConfig(), getClassLoader());
        databaseService.create(BankUser.class, UUID.class);
    }

}
