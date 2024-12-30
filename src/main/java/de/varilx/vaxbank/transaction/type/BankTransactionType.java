package de.varilx.vaxbank.transaction.type;


import de.varilx.utils.language.LanguageUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum BankTransactionType {

    ADD(LanguageUtils.getMessageString("add")),
    REMOVE(LanguageUtils.getMessageString("remove")),
    ADMIN_ADD(LanguageUtils.getMessageString("admin_add")),
    ADMIN_REMOVE(LanguageUtils.getMessageString("admin_remove")),
    ADMIN_SET(LanguageUtils.getMessageString("admin_set")),
    ADMIN_RESET(LanguageUtils.getMessageString("admin_reset"));

    String displayName;
}
