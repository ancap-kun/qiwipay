package ru.ancap.pay.plugin.player;

import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.pay.plugin.AncapPay;
import ru.ancap.pay.plugin.promocode.PromocodeAPI;
import ru.ancap.pay.plugin.promocode.exception.PromotionalCodeIsAlreadyUsedException;
import ru.ancap.pay.plugin.transaction.TransactionAPI;

public class PayPlayer {
    
    private final PathDatabase database;
    
    private PayPlayer(PathDatabase database) {
        this.database = database;
        if (!this.created()) this.create();
    }

    private void create() {
        this.database.write("balance", 0D);
    }

    private boolean created() {
        return this.database.isSet("");
    }

    public static PayPlayer get(String name) {
        return new PayPlayer(AncapPay.DATABASE.inner("players."+name));
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
        return this.database.readNumber("balance");
    }
    
    public void balance(double newBalance) {
        this.database.write("balance", newBalance);
    }
}

