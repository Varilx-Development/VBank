package de.varilx.vaxbank.listener;


import de.varilx.database.repository.Repository;
import de.varilx.vaxbank.VBank;
import de.varilx.vaxbank.user.BankUser;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.ArrayList;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConnectionListener implements Listener {

    VBank plugin;
    Repository<BankUser, UUID> userRepository;

    public ConnectionListener(VBank plugin) {
        this.plugin = plugin;
        this.userRepository = (Repository<BankUser, UUID>) plugin.getDatabaseService().getRepository(BankUser.class);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        userRepository.exists(player.getUniqueId()).thenAccept(status -> {
           if(status) return;
           BankUser bankUser = new BankUser();
           bankUser.setBalance(0.0);
           bankUser.setName(player.getName());
           bankUser.setUniqueId(player.getUniqueId());
           bankUser.setTransactions(new ArrayList<>());
           userRepository.insert(bankUser);

        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

}
