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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionsGui {

    Repository<BankUser, UUID> userRepository;
    Player holder;
    VBank plugin;
    DateFormat dateFormat;

    static String[] PATTERN = new String[] {
            "XXXXXXXXX",
            "XTTTTTTTX",
            "XTTTTTTTX",
            "XTTTTTTTX",
            "XTTTTTTTX",
            "BXXXXXXXN"
    };

    public TransactionsGui(Player holder, VBank plugin) {
        this.holder = holder;
        this.plugin = plugin;
        this.dateFormat = new SimpleDateFormat(LanguageUtils.getMessageString("date_format"));
        this.userRepository = (Repository<BankUser, UUID>) plugin.getDatabaseService().getRepository(BankUser.class);
        openGui();
    }

    private void openGui() {
        userRepository.findFirstById(holder.getUniqueId()).thenAccept(user -> {
            List<BankTransaction> transactions = user.getTransactions();
            transactions.sort(Comparator.comparingLong(BankTransaction::getTime).reversed());

            List<ClickableItem> paginationItems = new ArrayList<>();

            transactions.forEach(transaction -> {
                paginationItems.add(transactionItem(transaction));
            });

            GameInventory inventory = new GameInventoryBuilder(BaseAPI.getBaseAPI())
                    .inventoryName(LanguageUtils.getMessage("TransactionsGui.Title"))
                    .holder(holder)
                    .pattern(PATTERN)
                    .size(6 * 9)
                    .addItem('X', new ClickableItem(new ItemBuilder(Material.valueOf(LanguageUtils.getMessageString("TransactionsGui.Items.Placeholders.1.Material"))).name(Component.empty()).build()) {
                        @Override
                        public void handleClick(InventoryClickEvent inventoryClickEvent) {}
                    })
                    .enablePagination('T', 28, paginationItems)
                    .build();

            inventory.addItem('B', new ClickableItem(new SkullBuilder()
                    .setSkullTextureFromUrl(LanguageUtils.getMessageString("back_skull"))
                    .name(LanguageUtils.getMessage("TransactionsGui.Items.BackItem.Name"))
                    .lore(LanguageUtils.getMessageList("TransactionsGui.Items.Back.Lore"))
                    .build()) {
                @Override
                public void handleClick(InventoryClickEvent inventoryClickEvent) {
                    inventory.previousPage();
                }
            });
            inventory.addItem('N', new ClickableItem(new SkullBuilder()
                    .setSkullTextureFromUrl(LanguageUtils.getMessageString("next_skull"))
                    .name(LanguageUtils.getMessage("TransactionsGui.Items.NextItem.Name"))
                    .lore(LanguageUtils.getMessageList("TransactionsGui.Items.NextItem.Lore"))
                    .build()) {
                @Override
                public void handleClick(InventoryClickEvent inventoryClickEvent) {
                    inventory.nextPage();
                }
            });
            plugin.getServer().getScheduler().runTask(plugin, inventory::open);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    private ClickableItem transactionItem(BankTransaction transaction) {
        return new ClickableItem(new SkullBuilder()
                .setSkullTextureFromUrl((transaction.getType() == BankTransactionType.ADD ? LanguageUtils.getMessageString("transaction_item_add_skull") : LanguageUtils.getMessageString("transaction_item_remove_skull")))
                .name(LanguageUtils.getMessage("TransactionsGui.Items.TransactionItem.Name", Placeholder.parsed("date", dateFormat.format(transaction.getTime()))))
                .lore(LanguageUtils.getMessageList("TransactionsGui.Items.TransactionItem.Lore",
                        Placeholder.parsed("amount", MathUtils.formatNumber(transaction.getAmount())),
                        Placeholder.parsed("balance", MathUtils.formatNumber(transaction.getBalance())),
                        Placeholder.parsed("type", (transaction.getType() == BankTransactionType.ADD ? LanguageUtils.getMessageString("add") : LanguageUtils.getMessageString("remove")))
                ))
                .build()) {
            @Override
            public void handleClick(InventoryClickEvent inventoryClickEvent) {

            }
        };
    }

}
