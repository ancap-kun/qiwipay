package ru.ancap.pay.plugin.buy;

import lombok.AllArgsConstructor;
import ru.ancap.pay.plugin.config.QiwiConfig;

@AllArgsConstructor
public class Product {
    
    private final String name;
    private final double price;
    private final String giveCommand;
    
    public static Product get(String name) {
        return new Product(
                name,
                QiwiConfig.loaded().getDouble("donates."+name+".price"),
                QiwiConfig.loaded().getString("donates."+name+".command")
        );
    }
    
    public String name() {
        return this.name;
    }
    
    public double price() {
        return this.price;
    }
    
    public String giveCommand() {
        return this.giveCommand;
    }
    
}
