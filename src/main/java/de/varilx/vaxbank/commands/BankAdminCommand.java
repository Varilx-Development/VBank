package de.varilx.vaxbank.commands;


import de.varilx.command.VaxCommand;
import de.varilx.database.repository.Repository;
import de.varilx.utils.NumberUtils;
import de.varilx.utils.language.LanguageUtils;
import de.varilx.vaxbank.VBank;
import de.varilx.vaxbank.user.BankUser;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BankAdminCommand extends VaxCommand {

    VBank plugin;
    Repository<BankUser, UUID> userRepository;

    public BankAdminCommand(VBank plugin) {
        super(LanguageUtils.getMessageString("Commands.BankAdminCommand.Name"));
        this.plugin = plugin;
        this.userRepository = (Repository<BankUser, UUID>) plugin.getDatabaseService().getRepository(BankUser.class);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(sender instanceof Player player) {
            if(!player.hasPermission(LanguageUtils.getMessageString("Commands.BankAdminCommand.Permission"))) {
                player.sendMessage(LanguageUtils.getMessage("no_permission"));
                return false;
            }

            if(args.length == 0) {
                LanguageUtils.getMessageList("Commands.BankAdminCommand.Usage").forEach(player::sendMessage);
            } else if(args.length == 1) {
                handleBalance(player, args[0]);
            } else if(args.length == 2) {
                if(args[0].equalsIgnoreCase(LanguageUtils.getMessageString("Commands.BankAdminCommand.Arguments.Reset"))) {
                    handleReset(player, args[1]);
                }
            } else if(args.length == 3) {
                if(args[0].equalsIgnoreCase(LanguageUtils.getMessageString("Commands.BankAdminCommand.Arguments.Set"))) {
                    handleSet(player, args[1], args[2]);
                } else if(args[0].equalsIgnoreCase(LanguageUtils.getMessageString("Commands.BankAdminCommand.Arguments.Add"))) {
                    handleAdd(player, args[1], args[2]);
                } else if(args[0].equalsIgnoreCase(LanguageUtils.getMessageString("Commands.BankAdminCommand.Arguments.Remove"))) {
                    handleRemove(player, args[1], args[2]);
                }
            } else {
                LanguageUtils.getMessageList("Commands.BankAdminCommand.Usage").forEach(player::sendMessage);
            }

        }
        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if(sender instanceof Player player && player.hasPermission(LanguageUtils.getMessageString("Commands.BankAdminCommand.Permission"))) {
            if(args.length == 1) {
                List<String> arguments = new ArrayList<>();
                arguments.add(LanguageUtils.getMessageString("Commands.BankAdminCommand.Arguments.Add"));
                arguments.add(LanguageUtils.getMessageString("Commands.BankAdminCommand.Arguments.Remove"));
                arguments.add(LanguageUtils.getMessageString("Commands.BankAdminCommand.Arguments.Reset"));
                arguments.add(LanguageUtils.getMessageString("Commands.BankAdminCommand.Arguments.Set"));

                Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> arguments.add(onlinePlayer.getName()));

                String partialInput = args[args.length - 1].toLowerCase();
                List<String> filteredSuggestions = new ArrayList<>();
                for (String suggestion : arguments) {
                    if (suggestion.toLowerCase().startsWith(partialInput)) {
                        filteredSuggestions.add(suggestion);
                    }
                }

                return filteredSuggestions;
            }
        }
        return super.tabComplete(sender, alias, args);
    }

    private void handleBalance(Player player, String name) {
        userRepository.findByFieldName("name", name).thenAccept(user -> {
            if(user == null) {
                player.sendMessage(LanguageUtils.getMessage("user_not_found"));
                return;
            }
            player.sendMessage(LanguageUtils.getMessage("bank_balance",
                    Placeholder.parsed("balance", NumberUtils.formatDouble(user.getBalance())),
                    Placeholder.parsed("currency_name", LanguageUtils.getMessageString("currency_name"))
            ));
        });
    }

    private void handleRemove(Player player, String name, String amount) {
        userRepository.findByFieldName("name", name).thenAccept(user -> {
            if(user == null) {
                player.sendMessage(LanguageUtils.getMessage("user_not_found"));
                return;
            }
            if(!NumberUtils.isNumeric(amount)) {
                player.sendMessage(LanguageUtils.getMessage("input_no_number"));
                return;
            }

            double realAmount = Double.parseDouble(amount);

            if(realAmount <= 0) {
                player.sendMessage(LanguageUtils.getMessage("input_number_positive"));
                return;
            }

            if((user.getBalance() - realAmount) < 0) {
                player.sendMessage(LanguageUtils.getMessage("input_number_positive"));
                return;
            }

            user.removeBalance(realAmount);
            userRepository.save(user);
            player.sendMessage(LanguageUtils.getMessage("bank_remove_success", Placeholder.parsed("amount", NumberUtils.formatDouble(realAmount))));
        });
    }

    private void handleAdd(Player player, String name, String amount) {
        userRepository.findByFieldName("name", name).thenAccept(user -> {
            if(user == null) {
                player.sendMessage(LanguageUtils.getMessage("user_not_found"));
                return;
            }
            if(!NumberUtils.isNumeric(amount)) {
                player.sendMessage(LanguageUtils.getMessage("input_no_number"));
                return;
            }

            double realAmount = Double.parseDouble(amount);

            if(realAmount <= 0) {
                player.sendMessage(LanguageUtils.getMessage("input_number_positive"));
                return;
            }

            user.addBalance(realAmount);
            userRepository.save(user);
            player.sendMessage(LanguageUtils.getMessage("bank_add_success", Placeholder.parsed("amount", NumberUtils.formatDouble(realAmount))));
        });
    }

    private void handleSet(Player player, String name, String amount) {
        userRepository.findByFieldName("name", name).thenAccept(user -> {
            if(user == null) {
                player.sendMessage(LanguageUtils.getMessage("user_not_found"));
                return;
            }
            if(!NumberUtils.isNumeric(amount)) {
                player.sendMessage(LanguageUtils.getMessage("input_no_number"));
                return;
            }

            double realAmount = Double.parseDouble(amount);

            if(realAmount <= 0) {
                player.sendMessage(LanguageUtils.getMessage("input_number_positive"));
                return;
            }

            user.setBalance(realAmount);
            userRepository.save(user);
            player.sendMessage(LanguageUtils.getMessage("bank_set_success", Placeholder.parsed("balance", NumberUtils.formatDouble(user.getBalance()))));

        });
    }

    private void handleReset(Player player, String name) {
        userRepository.findByFieldName("name", name).thenAccept(user -> {
            if(user == null) {
                player.sendMessage(LanguageUtils.getMessage("user_not_found"));
                return;
            }
            user.setBalance(0.0);
            userRepository.save(user);
            player.sendMessage(LanguageUtils.getMessage("bank_reset_success"));
        });
    }

}
