/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.plugin.java.JavaPlugin
 */
package ru.ancap.pay.plugin.plugin;

import com.qiwi.billpayments.sdk.client.BillPaymentClient;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ancap.framework.api.nosql.database.Database;
import ru.ancap.framework.api.nosql.database.FileConfigurationDatabase;
import ru.ancap.framework.api.plugin.plugins.AncapPlugin;
import ru.ancap.pay.plugin.config.QiwiConfig;
import ru.ancap.pay.plugin.plugin.command.AncapPayCommandOperator;
import ru.ancap.pay.plugin.promocode.command.PromoCodeCommandOperator;
import ru.ancap.pay.plugin.donate.DonateCommandOperator;
import ru.ancap.pay.plugin.qiwi.QiwiModule;

public final class AncapPay extends AncapPlugin {
    public static JavaPlugin INSTANCE;
    public static Database DATABASE;
    public static final String MESSAGE_DOMAIN = "ru.ancap.pay.messages.";
    
    private BillPaymentClient qiwiClient;
    private QiwiModule qiwiModule;
    
    public void onEnable() {
        super.onEnable();
        this.setupInstance();
        this.setupDatabase();
        this.loadLocales();
        this.loadConfig();
        this.loadQiwiModule();
        this.registerExecutor("ancap-pay", new AncapPayCommandOperator());
        this.registerExecutor("donate", new DonateCommandOperator(this.getAncap().getTypeNameProvider(), qiwiModule));
        this.registerExecutor("promo-code", new PromoCodeCommandOperator(this.getAncap().getTypeNameProvider()));
    }

    private void loadConfig() {
        new QiwiConfig(
                this.getConfig()
        ).load();
    }

    private void loadQiwiModule() {
        this.qiwiClient = new BillPaymentClient(QiwiConfig.loaded().getString("acquiring.qiwi.token"));
        this.qiwiModule = new QiwiModule(this.qiwiClient);
    }

    private void setupDatabase() {
        DATABASE = new FileConfigurationDatabase(this, 10L);
    }

    private void setupInstance() {
        INSTANCE = this;
    }

    public void onDisable() {
    }
    
}

