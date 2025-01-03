package de.varilx.vaxbank.gui;


import de.varilx.BaseAPI;
import de.varilx.database.repository.Repository;
import de.varilx.inventory.GameInventory;
import de.varilx.inventory.builder.GameInventoryBuilder;
import de.varilx.inventory.item.ClickableItem;
import de.varilx.utils.Conversation;
import de.varilx.utils.MathUtils;
import de.varilx.utils.NumberUtils;
import de.varilx.utils.itembuilder.ItemBuilder;
import de.varilx.utils.itembuilder.SkullBuilder;
import de.varilx.utils.language.LanguageUtils;
import de.varilx.vaxbank.VBank;
import de.varilx.vaxbank.transaction.BankTransaction;
import de.varilx.vaxbank.transaction.type.BankTransactionType;
import de.varilx.vaxbank.user.BankUser;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
            GameInventory inventory = new GameInventoryBuilder(BaseAPI.get()).pattern(PATTERN)
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
                            holder.closeInventory();
                            Conversation conversation = new Conversation(plugin, holder, LanguageUtils.getMessage("conversation_prompt"), TimeUnit.SECONDS, 30);
                            conversation.addListener(new Conversation.ConvoListener() {
                                @Override
                                public boolean onMessage(Player player, Component component, String plain) {
                                    if(plain.toLowerCase().equalsIgnoreCase("cancel") || plain.toLowerCase().equalsIgnoreCase("abbrechen")) {
                                        conversation.cancel(true);
                                        openGui();
                                    }
                                    handleWithdraw(plain);
                                    return true;
                                }
                            });
                        }
                    })
                    .addItem('M', new ClickableItem(new SkullBuilder(Material.PLAYER_HEAD)
                            .setSkullTextureFromUrl(LanguageUtils.getMessageString("deposit_item_skull"))
                            .name(LanguageUtils.getMessage("Gui.Items.Deposit.Name"))
                            .lore(LanguageUtils.getMessageList("Gui.Items.Deposit.Lore"))
                            .build()) {
                        @Override
                        public void handleClick(InventoryClickEvent inventoryClickEvent) {
                            holder.closeInventory();
                            Conversation conversation = new Conversation(plugin, holder, LanguageUtils.getMessage("conversation_prompt"), TimeUnit.SECONDS, 30);
                            conversation.addListener(new Conversation.ConvoListener() {
                                @Override
                                public boolean onMessage(Player player, Component component, String plain) {
                                    if(plain.toLowerCase().equalsIgnoreCase("cancel") || plain.toLowerCase().equalsIgnoreCase("abbrechen")) {
                                        conversation.cancel(true);
                                        openGui();
                                    }
                                    handleDeposit(plain);
                                    return true;
                                }
                            });
                        }
                    })
                    .addItem('Y', new ClickableItem(new ItemBuilder(Material.HEAVY_CORE)
                            .name(LanguageUtils.getMessage("Gui.Items.Transactions.Name"))
                            .lore(LanguageUtils.getMessageList("Gui.Items.Transactions.Lore"))
                            .build()) {
                        @Override
                        public void handleClick(InventoryClickEvent inventoryClickEvent) {
                            new TransactionsGui(holder, plugin);
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

    private void handleWithdraw(String input) {
        if(!NumberUtils.isNumeric(input)) {
            holder.sendMessage(LanguageUtils.getMessage("input_no_number"));
            return;
        }

        double amount = Double.parseDouble(input);

        if(amount <= 0) {
            holder.sendMessage(LanguageUtils.getMessage("input_number_positive"));
            return;
        }


        userRepository.findFirstById(holder.getUniqueId()).thenAccept(user -> {
            if(user.getBalance() < amount) {
                holder.sendMessage(LanguageUtils.getMessage("not_enough_money"));
                return;
            }

            user.removeBalance(amount);
            plugin.getEconomy().withdrawPlayer(holder, amount);
            holder.sendMessage(LanguageUtils.getMessage("bank_withdraw_success",
                    Placeholder.parsed("amount", NumberUtils.formatDouble(amount)),
                    Placeholder.parsed("currency_name", LanguageUtils.getMessageString("currency_name"))
            ));

            BankTransaction transaction = new BankTransaction();
            transaction.setAccountId(holder.getUniqueId());
            transaction.setTime(System.currentTimeMillis());
            transaction.setType(BankTransactionType.REMOVE);
            transaction.setAmount(amount);
            transaction.setBalance(user.getBalance());
            transaction.setUser(user);
            user.addTransaction(transaction);
            userRepository.save(user);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    private void handleDeposit(String input) {
        if(!NumberUtils.isNumeric(input)) {
            holder.sendMessage(LanguageUtils.getMessage("input_no_number"));
            return;
        }
        double amount = Double.parseDouble(input);

        if(!plugin.getEconomy().has(holder, amount)) {
            holder.sendMessage(LanguageUtils.getMessage("not_enough_money"));
            return;
        }

        if(amount <= 0) {
            holder.sendMessage(LanguageUtils.getMessage("input_number_positive"));
            return;
        }

        plugin.getEconomy().withdrawPlayer(holder, amount);

        userRepository.findFirstById(holder.getUniqueId()).thenAccept(user -> {
            user.addBalance(amount);
            holder.sendMessage(LanguageUtils.getMessage("bank_deposit_success",
                    Placeholder.parsed("amount", NumberUtils.formatDouble(amount)),
                    Placeholder.parsed("currency_name", LanguageUtils.getMessageString("currency_name"))
            ));

            BankTransaction transaction = new BankTransaction();
            transaction.setAccountId(holder.getUniqueId());
            transaction.setTime(System.currentTimeMillis());
            transaction.setType(BankTransactionType.ADD);
            transaction.setAmount(amount);
            transaction.setBalance(user.getBalance());
            transaction.setUser(user);

            user.addTransaction(transaction);

            userRepository.save(user);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }


}
