package de.varilx.vaxbank.user;


import de.varilx.database.id.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID uniqueId;

    String name;
    double balance;
    List<UUID> transactions;

}
