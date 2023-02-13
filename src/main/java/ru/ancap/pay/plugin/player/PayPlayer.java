package ru.ancap.pay.plugin.player;

import lombok.AllArgsConstructor;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.pay.plugin.AncapPay;
import ru.ancap.pay.plugin.promocode.PromocodeAPI;
import ru.ancap.pay.plugin.promocode.exception.PromotionalCodeIsAlreadyUsedException;
import ru.ancap.pay.plugin.transaction.TransactionAPI;
import ru.ancap.commons.AncapDebug;

@AllArgsConstructor
public class PayPlayer {
    
    private final String name;
    private final PathDatabase database;

    public static PayPlayer get(String name) {
        return new PayPlayer(name, AncapPay.DATABASE.inner("players."+name));
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
    
    public double balance() {
        return this.database.getDouble("balance");
    }
    
    public void balance(double newBalance) {
        AncapDebug.debug("new balance", newBalance);
        this.database.write("balance", newBalance);
    }
}

