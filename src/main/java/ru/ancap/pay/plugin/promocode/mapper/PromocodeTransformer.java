package ru.ancap.pay.plugin.promocode.mapper;

import ru.ancap.framework.api.command.commands.transformer.basic.AbstractTransformer;
import ru.ancap.pay.plugin.promocode.PromocodeAPI;

public class PromocodeTransformer extends AbstractTransformer<PromocodeAPI> {
    
    public PromocodeTransformer() {
        super(PromocodeAPI.class);
    }
    
    @Override
    protected PromocodeAPI provide(String name) {
        PromocodeAPI promocode = PromocodeAPI.find(name);
        if (promocode == null) throw new RuntimeException("Promocode not found");
        return promocode;
    }
    
}
