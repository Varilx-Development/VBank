![v-bank](https://github.com/user-attachments/assets/025b2a41-1e36-497f-baa9-f7267d79f205)

![Discord](https://img.shields.io/discord/1322873747535040512)
![Build Status](https://img.shields.io/github/actions/workflow/status/Varilx-Development/VDiscordIntegration/build.yml?branch=main)
![Release](https://img.shields.io/github/v/release/Varilx-Development/VDiscordIntegration)

# VBank Configuration

A bank plugin that works with **Vault** and fully Customizable!

---

# Preview
![bank-show](https://github.com/user-attachments/assets/e972256f-8a85-4f31-a897-3c2deb15b690)


## Configuration Overview

### 1. **Database**
Define the database type and connection details. The plugin supports:
- **MongoDB**: Specify a connection string and database name.
- **SQL**: Provide a JDBC connection string. (No username/password is required for SQLite.)

### 2. **Custom Messages**
Customize messages for server startup, player join/quit, and Discord chat using the MiniMessage format.
We currently support: `de` and `en`

### 3. **Commands**
`/bank` - Opens the GUI.

`/bankadmin add (player) (amount)` - Giving the Player Bank Balance.

`/bankadmin remove (player) (amount)` - Removing the Player Bank Balance.

`/bankadmin set (player) (amount)` - Sets the Balance on the Amount.

`/bankadmin reset (player)` - Resets fully the Bank Balance from the Player.


### 4. **Permissions** 
`vbank.bankadmin` | Fully configurable in the Config.yml.
- Permission to use the `/bankadmin` Command.

---

## Setup Instructions 

1. Download and install the plugin on your Minecraft server.
2. Configure the `config.yml` file with your preferred settings:
    - Set the database type and connection details.
    - Define custom messages using MiniMessage.
3. Restart the server to apply the changes.

---

## Example config

```yaml
language: en
```

---

## Example Message Configuration

```yaml
# Using Minimessage https://docs.advntr.dev/minimessage/format.html

# General Messages
currency_name: "coins"
prefix: "<b><dark_gray>[<gradient:#119143:#053E88>VBank</gradient><dark_gray>]<reset><gray> "
no_permission: "<prefix><red>You don't have Permission to do this!"
user_not_found: "<prefix><red>This user does not exist"
input_no_number: "<prefix><red>Enter a valid number"
not_enough_money: "<prefix>You don't have enough money"
bank_deposit_success: "<prefix>u have <green>successfully <gray>deposited <yellow><amount> <currency_name> <gray>into your bank account"
bank_withdraw_success: "<prefix>u have <green>successfully <gray>withdrawed <yellow><amount> <currency_name> <gray>from your bank account"
bank_reset_success: "<prefix><green>The player's bank account has been reset"
bank_set_success: "<prefix><gray>The player's bank account has been set to <yellow><balance>"
bank_add_success: "<prefix><gray>You added <yellow><amount> <gray>to players bank account"
bank_remove_success: "<prefix><gray>You removed <yellow><amount> <gray>to players bank account"
bank_balance: "<prefix>The bank account has <yellow><balance><currency_name>"
input_number_positive: "<prefix>Enter a positive number"
conversation_prompt: "<prefix>Please enter an amount. <newline> <prefix>Write <dark_gray>'<red>cancel<dark_gray>'<gray> to cancel"
date_format: "MM/dd/yyyy - hh:mm a"
add: "<green>Add"
remove: "<red>Remove"
admin_add: "<green>Add <dark_gray>(<red>Admin<dark_gray>)"
admin_remove: "<green>Remove <dark_gray>(<red>Admin<dark_gray>)"
admin_set: "<green>Set <dark_gray>(<red>Admin<dark_gray>)"
admin_reset: "<green>Reset <dark_gray>(<red>Admin<dark_gray>)"

# Skulls
withdraw_item_skull: "https://textures.minecraft.net/texture/482c23992a02725d9ed1bcd90fd0307c8262d87e80ce6fac8078387de18d0851"
deposit_item_skull: "https://textures.minecraft.net/texture/177bb66fc73a97cefcb3a4bfdccb12281f44dd326ccd0ff39d47e985bfeff343"
transaction_item_add_skull: "https://textures.minecraft.net/texture/5ff31431d64587ff6ef98c0675810681f8c13bf96f51d9cb07ed7852b2ffd1"
transaction_item_remove_skull: "https://textures.minecraft.net/texture/4e4b8b8d2362c864e062301487d94d3272a6b570afbf80c2c5b148c954579d46"
back_skull: "https://textures.minecraft.net/texture/bd8a99db2c37ec71d7199cd52639981a7513ce9cca9626a3936f965b131193"
next_skull: "https://textures.minecraft.net/texture/3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716"
admin_reset_skull: "https://textures.minecraft.net/texture/e9cdb9af38cf41daa53bc8cda7665c509632d14e678f0f19f263f46e541d8a30"

# Guis
Gui:
  Title: "<prefix>Bankaccount"
  Items:
    Placeholders:
      1:
        Material: "GRAY_STAINED_GLASS_PANE"
      2:
        Material: "WHITE_STAINED_GLASS_PANE"
    Balance:
      Material: "SUNFLOWER"
      Name: "<gold>Balance"
      Lore:
        - ""
        - "<dark_gray>» <yellow><balance> <currency_name>"
    Withdraw:
      Name: "<red>Withdraw money"
      Lore:
        - ""
        - "<!i><dark_gray>» <gray>Click here to withdraw money"
    Deposit:
      Name: "<red>Deposit money"
      Lore:
        - ""
        - "<!i><dark_gray>» <gray>Click here to deposit money"
    Transactions:
      Name: "<red>Transactions"
      Lore:
        - ""
        - "<!i><dark_gray>» <gray>Click here to see your transactions"

TransactionsGui:
  Title: "<prefix> Transactions"
  Items:
    Placeholders:
      1:
        Material: "GRAY_STAINED_GLASS_PANE"
    TransactionItem:
      Name: "<red>Transaction <dark_gray>| <yellow><date>"
      Lore:
        - ""
        - "<dark_gray>| <gray>Amount <dark_gray>» <yellow><amount>"
        - "<dark_gray>| <gray>Balance <dark_gray>» <yellow><balance>"
        - "<dark_gray>| <gray>Type <dark_gray>» <yellow><type>"
    BackItem:
      Name: "<red>Back"
      Lore:
        - ""
        - "<dark_gray>| <gray>Click here to go back one page"
    NextItem:
      Name: "<green>Next"
      Lore:
        - ""
        - "<dark_gray>| <gray>Klicke hier um zur nächsten Seite zu gelangen"

# Commands
Commands:
  BankCommand:
    Name: 'bank'
    Arguments:
      Add: 'add'
      Remove: 'remove'
  BankAdminCommand:
    Name: 'bankadmin'
    Permission: 'vbank.bankadmin'
    Arguments:
      Add: 'add'
      Remove: 'remove'
      Reset: 'reset'
      Set: 'set'
    Usage:
      - "<prefix>BankAdmin help<dark_gray>:"
      - "<prefix>Use <yellow>/bankadmin add <dark_gray><<yellow>Player<dark_gray>> <<yellow>Amount<dark_gray>>"
      - "<prefix>Use <yellow>/bankadmin remove <dark_gray><<yellow>Player<dark_gray>> <<yellow>Amount<dark_gray>>"
      - "<prefix>Use <yellow>/bankadmin set <dark_gray><<yellow>Player<dark_gray>> <<yellow>Amount<dark_gray>>"
      - "<prefix>Use <yellow>/bankadmin reset <dark_gray><<yellow>Player<dark_gray>>"
```

---

## Example Database Configuration

```yaml
type: Sqlite # Avaiable types: mongo, mysql, sqlite


# MONGO
Mongo:
  connection-string: "mongodb://<username>:<password>@<host>:<port>/"
  database: "db"


# SQL
SQL:
  connection-string: "jdbc:sqlite:plugins/VDiscordIntegration/database.db"
  username: "username" # Not required for sqlite
  password: "password" # Not required for sqlite
```


---

---
## Requirements
* VaultAPI
---



## Notes

- The MiniMessage format is highly flexible for styling and formatting messages. Refer to the [MiniMessage documentation](https://docs.advntr.dev/minimessage/format.html) for more details.
- SQLite is the simplest database option as it doesn’t require additional setup.

<a href="https://discord.gg/ZPyb9g6Gs4">
    <img src="https://github.com/user-attachments/assets/e2c942ae-d79a-4606-b4b0-240fd92c9a90" alt="Join our Discord for help" width="400">
</a>
