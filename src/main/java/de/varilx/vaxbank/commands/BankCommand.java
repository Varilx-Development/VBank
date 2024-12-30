package de.varilx.vaxbank.commands;


import de.varilx.BaseAPI;
import de.varilx.command.VaxCommand;
import de.varilx.config.Configuration;
import de.varilx.database.repository.Repository;
import de.varilx.utils.NumberUtils;
import de.varilx.utils.language.LanguageUtils;
import de.varilx.vaxbank.VBank;
import de.varilx.vaxbank.transaction.BankTransaction;
import de.varilx.vaxbank.transaction.type.BankTransactionType;
import de.varilx.vaxbank.user.BankUser;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BankCommand extends VaxCommand {

    VBank plugin;
    Repository<BankUser, UUID> userRepository;

    public BankCommand(VBank plugin) {
        super(LanguageUtils.getMessageString("Commands.BankCommand.Name"));
        this.plugin = plugin;
        this.userRepository = (Repository<BankUser, UUID>) plugin.getDatabaseService().getRepositoryMap().get(BankUser.class);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(sender instanceof Player player) {

            if(args.length == 0) {
                printUsage(player);
            } else if(args.length == 2) {
                if(args[0].toLowerCase().equalsIgnoreCase(LanguageUtils.getMessageString("Commands.BankCommand.Arguments.Add"))) {
                    if(!NumberUtils.isNumeric(args[1])) {
                        player.sendMessage(LanguageUtils.getMessage("input_no_number"));
                        return false;
                    }

                    double amount = Double.parseDouble(args[1]);

                    if(!plugin.getEconomy().has(player, amount)) {
                        player.sendMessage(LanguageUtils.getMessage("not_enough_money"));
                        return false;
                    }

                    plugin.getEconomy().withdrawPlayer(player, amount);

                    userRepository.findFirstById(player.getUniqueId()).thenAccept(user -> {
                        if(user == null) {
                            System.out.println("User is null");
                            return;
                        }
                        user.setBalance(user.getBalance() + amount);
                        player.sendMessage(LanguageUtils.getMessage("bank_deposit_success",
                                Placeholder.parsed("amount", NumberUtils.formatDouble(amount)),
                                Placeholder.parsed("currency_name", LanguageUtils.getMessageString("currency_name"))
                        ));

                        BankTransaction transaction = new BankTransaction();
                        transaction.setTime(System.currentTimeMillis());
                        transaction.setType(BankTransactionType.ADD);
                        transaction.setAmount(amount);
                        transaction.setBalance(user.getBalance());
                        transaction.setUser(user);

                        user.getTransactions().add(transaction);

                        userRepository.save(user);
                    }).exceptionally(throwable -> {
                        throwable.printStackTrace();
                        return null;
                    });

                } else if(args[0].toLowerCase().equalsIgnoreCase(LanguageUtils.getMessageString("Commands.BankCommand.Arguments.Remove"))) {

                }
            }

        }
        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if(args.length == 1) {
            return List.of(
                    LanguageUtils.getMessageString("Commands.BankCommand.Arguments.Add"),
                    LanguageUtils.getMessageString("Commands.BankCommand.Arguments.Remove")
            );
        }
        return super.tabComplete(sender, alias, args);
    }

    private void printUsage(Player player) {
        String language = Optional.ofNullable(BaseAPI.getBaseAPI().getConfiguration().getConfig().getString("language")).orElse("en");
        Configuration langConfig = BaseAPI.getBaseAPI().getLanguageConfigurations().get(language);

        List<TagResolver> baseResolvers = new ArrayList<>();

        @Nullable String prefix = langConfig.getConfig().getString("prefix");
        if (prefix != null) baseResolvers.add(Placeholder.parsed("prefix", prefix));

        List<Component> components = new ArrayList<>();
        langConfig.getConfig().getStringList("Commands.BankCommand.Usage")
                .forEach(line -> components.add(LanguageUtils.getMessage(line, baseResolvers.toArray(TagResolver[]::new))));

        components.forEach(player::sendMessage);
    }

}
