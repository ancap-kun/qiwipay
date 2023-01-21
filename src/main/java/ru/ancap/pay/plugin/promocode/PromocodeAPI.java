/*
 * Decompiled with CFR 0.150.
 */
package ru.ancap.pay.plugin.promocode;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ancap.framework.api.nosql.database.Database;
import ru.ancap.pay.plugin.plugin.AncapPay;
import ru.ancap.pay.plugin.promocode.exception.IllegalPromotionalCodeTypeException;
import ru.ancap.pay.plugin.promocode.exception.PromotionalCodeIsAlreadyUsedException;
import ru.ancap.pay.plugin.promocode.exception.PromotionalCodeIsExpiredException;
import ru.ancap.pay.plugin.promocode.exception.PromotionalCodeIsSpentException;
import ru.ancap.pay.plugin.player.PayPlayer;

@AllArgsConstructor
public class PromocodeAPI {
    
    private final String name;
    private final Database database;
    
    @Nullable
    public static PromocodeAPI find(@NotNull String name) {
        if (!AncapPay.DATABASE.isSet("promotional-codes."+name)) return null;
        return new PromocodeAPI(name);
    }
    
    @NotNull
    public static PromocodeAPI create(String name, PromocodeType type, double value, long usages, long expiration) {
        PromocodeAPI promocodeAPI = new PromocodeAPI(name);
        Database db = promocodeAPI.database;
        switch (type) {
            case BONUS -> {
                db.write("type", "bonus");
                db.write("bonus", value);
            }
            case FIXED -> {
                db.write("type", "fixed");
                db.write("reward", value);
            }
        }
        db.write("expirationDate", 3600000L * expiration + System.currentTimeMillis());
        db.write("usages", usages);
        return promocodeAPI;
    }
        
    private PromocodeAPI(String name) {
        this(
                name,
                AncapPay.DATABASE.inner("promotional-codes."+name)
        );
    }
    
    @NotNull
    public PromocodeType getType() {
        return PromocodeType.valueOf(database.getString("type").toUpperCase());
    }
    
    public double getReward() {
        return database.getDouble("reward");
    }
    
    public double getBonus() {
        return database.getDouble("bonus");
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public boolean expired() {
        return this.getExpirationDate() < System.currentTimeMillis();
    }
    
    public long getExpirationDate() {
        return database.getNumber("expiration-date");
    }
    
    public void disable() {
        database.nullify();
    }

    public void use(String playerName) throws 
            PromotionalCodeIsSpentException, 
            PromotionalCodeIsAlreadyUsedException,
            IllegalPromotionalCodeTypeException,
            PromotionalCodeIsExpiredException {
        long usagesLast = this.getUsages();
        if (usagesLast <= 0) throw new PromotionalCodeIsSpentException();
        if (this.getType() != PromocodeType.FIXED) throw new IllegalPromotionalCodeTypeException();
        if (this.expired()) throw new PromotionalCodeIsExpiredException();
        PayPlayer player = PayPlayer.get(playerName);
        player.use(this);
        database.write("usages", usagesLast - 1);
    }

    public long getUsages() {
        return database.getNumber("usages");
    }

    public static double applyBonus(PromocodeAPI promocodeAPI, double money) {
        if (promocodeAPI == null) {
            return money;
        }
        PromocodeType type = promocodeAPI.getType();
        if (type == PromocodeType.BONUS) {
            return money * promocodeAPI.getBonus();
        }
        return money;
    }
}

