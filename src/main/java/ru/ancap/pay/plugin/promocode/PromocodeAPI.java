/*
 * Decompiled with CFR 0.150.
 */
package ru.ancap.pay.plugin.promocode;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.pay.plugin.AncapPay;
import ru.ancap.pay.plugin.player.PayPlayer;
import ru.ancap.pay.plugin.promocode.exception.IllegalPromotionalCodeTypeException;
import ru.ancap.pay.plugin.promocode.exception.PromotionalCodeIsAlreadyUsedException;
import ru.ancap.pay.plugin.promocode.exception.PromotionalCodeIsExpiredException;
import ru.ancap.pay.plugin.promocode.exception.PromotionalCodeIsSpentException;

@AllArgsConstructor
@ToString @EqualsAndHashCode
public class PromocodeAPI {
    
    private final String name;
    private final PathDatabase database;
    
    @Nullable
    public static PromocodeAPI find(@NotNull String name) {
        if (!AncapPay.DATABASE.isSet("promotional-codes."+name)) return null;
        return new PromocodeAPI(name);
    }
    
    @NotNull
    public static PromocodeAPI create(String name, PromocodeType type, double value, long usages, long expiration) {
        PromocodeAPI promocodeAPI = new PromocodeAPI(name);
        PathDatabase db = promocodeAPI.database;
        switch (type) {
            case BONUS:
                db.write("type", "bonus");
                db.write("bonus", value);
                break;
            case FIXED:
                db.write("type", "fixed");
                db.write("reward", value);
                break;
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
        return PromocodeType.valueOf(database.readString("type").toUpperCase());
    }
    
    public double getReward() {
        return database.readNumber("reward");
    }
    
    public double getBonus() {
        return database.readNumber("bonus");
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public boolean expired() {
        return this.getExpirationDate() < System.currentTimeMillis();
    }
    
    public long getExpirationDate() {
        return database.readInteger("expiration-date");
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
        return database.readInteger("usages");
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

