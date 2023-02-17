/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.plugin.java.JavaPlugin
 */
package ru.ancap.pay.plugin;

import com.qiwi.billpayments.sdk.client.BillPaymentClient;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ancap.framework.command.api.commands.object.executor.CommandOperator;
import ru.ancap.framework.database.nosql.ConfigurationDatabase;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.framework.plugin.api.AncapPlugin;
import ru.ancap.framework.plugin.api.information.AuthorsSupplier;
import ru.ancap.pay.plugin.balance.Rewarder;
import ru.ancap.pay.plugin.balance.WalletRepresenter;
import ru.ancap.pay.plugin.buy.ProductSeller;
import ru.ancap.pay.plugin.config.QiwiConfig;
import ru.ancap.pay.plugin.donate.DonateAcceptor;
import ru.ancap.pay.plugin.promocode.command.PromocodeOperator;
import ru.ancap.pay.plugin.qiwi.QiwiModule;

public final class AncapPay extends AncapPlugin {
    public static JavaPlugin INSTANCE;
    public static PathDatabase DATABASE;
    
    private BillPaymentClient qiwiClient;
    private CommandOperator authors;
    private QiwiModule qiwiModule;
    
    public void onEnable() {
        super.onEnable();
        this.setupInstance();
        this.setupDatabase();
        this.loadLocales();
        this.loadConfig();
        this.loadQiwiModule();
        this.loadAuthorsSupplier();
        this.registerExecutor("ancap-pay", this.authors);
        this.registerExecutor("donate", new DonateAcceptor(this.qiwiModule));
        this.registerExecutor("promo-code", new PromocodeOperator(this.authors));
        this.registerExecutor("wallet", new WalletRepresenter());
        this.registerExecutor("buy", new ProductSeller());
        this.registerExecutor("reward", new Rewarder());
    }

    private void loadAuthorsSupplier() {
        this.authors = new AuthorsSupplier(this, "plugin-info"); 
    }

    private void loadConfig() {
        new QiwiConfig(this.getConfiguration()).load();
    }

    private void loadQiwiModule() {
        this.qiwiClient = new BillPaymentClient(QiwiConfig.loaded().getString("acquiring.qiwi.token"));
        this.qiwiModule = new QiwiModule(this.qiwiClient);
    }

    private void setupDatabase() {
        AncapPay.DATABASE = ConfigurationDatabase.builder()
                .autoSave(10)
                .plugin(this)
                .build();
    }

    private void setupInstance() {
        AncapPay.INSTANCE = this;
    }
    
}

