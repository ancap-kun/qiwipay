package ru.ancap.pay.plugin.config;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.bukkit.configuration.ConfigurationSection;

@RequiredArgsConstructor
public class QiwiConfig {
    
    private static QiwiConfig INSTANCE;
    
    @Delegate
    private final ConfigurationSection section;
    
    public static QiwiConfig loaded() {
        return INSTANCE;
    }
    
    public void load() {
        QiwiConfig.INSTANCE = this;
    }
}
