package ru.ancap.pay.plugin.donate;

import org.bukkit.Bukkit;
import ru.ancap.commons.debug.AncapDebug;
import ru.ancap.pay.plugin.AncapPay;

public class QiwiPayBukkit {
    
    public static void sendConsoleCommand(String command) {
        Bukkit.getScheduler().callSyncMethod(AncapPay.INSTANCE, () -> {
            AncapDebug.debug("called sync method");
            try {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                Bukkit.getConsoleSender().sendMessage("Message to console sender");
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return Void.TYPE;
        });
    }
    
}
