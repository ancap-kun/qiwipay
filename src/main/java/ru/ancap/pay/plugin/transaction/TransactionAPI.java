package ru.ancap.pay.plugin.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.pay.plugin.AncapPay;
import ru.ancap.pay.plugin.promocode.PromocodeAPI;

import java.util.UUID;

@AllArgsConstructor
public class TransactionAPI {
    
    @Getter
    private final String id;
    private final PathDatabase database;
    
    @Nullable
    public static TransactionAPI find(String id) {
        PathDatabase database= AncapPay.DATABASE.inner("transactions.list."+id);
        if (!database.isSet("name")) return null;
        return new TransactionAPI(id, database);
    }

    public static TransactionAPI create(long currentTimeMillis, Long amount, String playerName, @Nullable String promocode) {
        String uuid = UUID.randomUUID().toString();
        PathDatabase database = AncapPay.DATABASE.inner("transactions.list."+uuid);
        database.write("donater-name", playerName);
        database.write("payed", amount);
        database.write("time", currentTimeMillis);
        database.write("promocode-used", promocode);
        return new TransactionAPI(uuid, database);
    }
    
    @NotNull
    public String getDonater() {
        return this.database.getString("donater-name");
    }
    
    public long getPayedAmount() {
        return this.database.getNumber("payed");
    }
    
    public long getTransactionTime() {
        return this.database.getNumber("time");
    }
    
    @Nullable
    public PromocodeAPI usedPromocode() {
        String promocodeName = this.database.getString("promocode-used");
        return promocodeName == null ? null : PromocodeAPI.find(promocodeName);
    }
    
}
