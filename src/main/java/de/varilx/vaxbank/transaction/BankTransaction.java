package de.varilx.vaxbank.transaction;

import de.varilx.database.id.Id;
import de.varilx.vaxbank.transaction.type.BankTransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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

    @Id
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID transactionId;

    UUID sender, receiver;
    double amount;
    BankTransactionType type;

}
