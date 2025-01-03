package de.varilx.vaxbank.transaction;

import de.varilx.database.id.MongoId;
import de.varilx.vaxbank.transaction.type.BankTransactionType;
import de.varilx.vaxbank.user.BankUser;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankTransaction {

    @MongoId
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID transactionId;

    UUID accountId;
    double amount, balance;
    BankTransactionType type;
    long time;

    @ManyToOne
    BankUser user;

}
