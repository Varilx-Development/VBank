package de.varilx.vaxbank.listener;


import de.varilx.vaxbank.VBank;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.event.Listener;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConnectionListener implements Listener {

    VBank plugin;

    public ConnectionListener(VBank plugin) {
        this.plugin = plugin;
    }
}
