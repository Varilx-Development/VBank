package de.varilx.vaxbank.user;


import de.varilx.database.id.Id;
import de.varilx.vaxbank.transaction.BankTransaction;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankUser {

    @Id
    @jakarta.persistence.Id
    UUID uniqueId;

    String name;
    double balance;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<BankTransaction> transactions;

    public void addBalance(double amount) {
        this.balance += amount;
    }

    public void removeBalance(double amount) {
        this.balance -= amount;
    }

    public void addTransaction(BankTransaction transaction) {
        this.transactions.add(transaction);
    }

}
