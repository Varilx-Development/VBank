package de.varilx.vaxbank.gui;


import de.varilx.BaseAPI;
import de.varilx.database.repository.Repository;
import de.varilx.inventory.GameInventory;
import de.varilx.inventory.builder.GameInventoryBuilder;
import de.varilx.inventory.item.ClickableItem;
import de.varilx.utils.MathUtils;
import de.varilx.utils.itembuilder.ItemBuilder;
import de.varilx.utils.itembuilder.SkullBuilder;
import de.varilx.utils.language.LanguageUtils;
import de.varilx.vaxbank.VBank;
import de.varilx.vaxbank.user.BankUser;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BankGui {

    Repository<BankUser, UUID> userRepository;
    VBank plugin;

    Player holder;

    static String[] PATTERN = new String[] {
            "GGGGGGGGG",
            "GWWWWWWWG",
            "GWTTBTTWG",
            "GWXTYTMWG",
            "GWWWWWWWG",
            "GGGGGGGGG"
    };

    public BankGui(VBank plugin, Player holder) {
        this.plugin = plugin;
        this.holder = holder;
        this.userRepository = (Repository<BankUser, UUID>) plugin.getDatabaseService().getRepository(BankUser.class);
        openGui();
    }

    private void openGui() {
        userRepository.findFirstById(holder.getUniqueId()).thenAccept(user -> {
            GameInventory inventory = new GameInventoryBuilder(BaseAPI.getBaseAPI()).pattern(PATTERN)
                    .inventoryName(LanguageUtils.getMessage("Gui.Title"))
                    .size(6 * 9).addItem('G', new ClickableItem(new ItemBuilder(Material.valueOf(LanguageUtils.getMessageString("Gui.Items.Placeholders.1.Material")))
                            .name(Component.empty()).build()) {
                        @Override
                        public void handleClick(InventoryClickEvent inventoryClickEvent) {

                        }
                    }).addItem('W', new ClickableItem(new ItemBuilder(Material.valueOf(LanguageUtils.getMessageString("Gui.Items.Placeholders.2.Material")))
                            .name(Component.empty()).build()) {
                        @Override
                        public void handleClick(InventoryClickEvent inventoryClickEvent) {

                        }
                    })
                    .addItem('B', new ClickableItem(new ItemBuilder(Material.valueOf(LanguageUtils.getMessageString("Gui.Items.Balance.Material")))
                            .name(LanguageUtils.getMessage("Gui.Items.Balance.Name"))
                            .lore(LanguageUtils.getMessageList("Gui.Items.Balance.Lore",
                                    Placeholder.parsed("balance", MathUtils.formatNumber(user.getBalance())),
                                    Placeholder.parsed("currency_name", LanguageUtils.getMessageString("currency_name"))))
                            .build()) {
                        @Override
                        public void handleClick(InventoryClickEvent inventoryClickEvent) {
                        }
                    })
                    .addItem('X', new ClickableItem(new SkullBuilder(Material.PLAYER_HEAD)
                            .setSkullTextureFromUrl(LanguageUtils.getMessageString("withdraw_item_skull"))
                            .name(LanguageUtils.getMessage("Gui.Items.Withdraw.Name"))
                            .lore(LanguageUtils.getMessageList("Gui.Items.Withdraw.Lore"))
                            .build()) {
                        @Override
                        public void handleClick(InventoryClickEvent inventoryClickEvent) {

                        }
                    })
                    .addItem('M', new ClickableItem(new SkullBuilder(Material.PLAYER_HEAD)
                            .setSkullTextureFromUrl(LanguageUtils.getMessageString("deposit_item_skull"))
                            .name(LanguageUtils.getMessage("Gui.Items.Deposit.Name"))
                            .lore(LanguageUtils.getMessageList("Gui.Items.Deposit.Lore"))
                            .build()) {
                        @Override
                        public void handleClick(InventoryClickEvent inventoryClickEvent) {

                        }
                    })
                    .addItem('Y', new ClickableItem(new ItemBuilder(Material.HEAVY_CORE)
                            .name(LanguageUtils.getMessage("Gui.Items.Transactions.Name"))
                            .lore(LanguageUtils.getMessageList("Gui.Items.Transactions.Lore"))
                            .build()) {
                        @Override
                        public void handleClick(InventoryClickEvent inventoryClickEvent) {

                        }
                    })
                    .holder(holder)
                    .build();
            plugin.getServer().getScheduler().runTask(plugin, inventory::open);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

}
