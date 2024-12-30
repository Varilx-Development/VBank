package de.varilx.vaxbank.transaction;


import de.varilx.vaxbank.transaction.type.BankTransactionType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankTransaction {

    UUID sender, receiver;
    double amount;
    BankTransactionType type;

}
