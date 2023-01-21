/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package ru.ancap.pay.plugin.player;

import lombok.AllArgsConstructor;
import ru.ancap.framework.api.nosql.database.Database;
import ru.ancap.pay.plugin.plugin.AncapPay;
import ru.ancap.pay.plugin.promocode.PromocodeAPI;
import ru.ancap.pay.plugin.promocode.exception.PromotionalCodeIsAlreadyUsedException;
import ru.ancap.pay.plugin.transaction.TransactionAPI;

@AllArgsConstructor
public class PayPlayer {
    private final String name;
    private final Database database;

    public static PayPlayer get(String name) {
        return new PayPlayer(name, AncapPay.DATABASE.inner("players"));
    }
    
    public void saveDonate(TransactionAPI transaction) {
        this.database.add("donates", ""+transaction.getId());
    }
    
    public void use(PromocodeAPI promocodeAPI) throws PromotionalCodeIsAlreadyUsedException {
        if (this.used(promocodeAPI)) throw new PromotionalCodeIsAlreadyUsedException();
        this.database.add("promocodes-used", promocodeAPI.getName());
    }

    public boolean used(PromocodeAPI promocode) {
        return this.database.contains("promocodes-used", promocode.getName());
    }
}

