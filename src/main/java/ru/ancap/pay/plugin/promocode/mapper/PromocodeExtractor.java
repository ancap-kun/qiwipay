package ru.ancap.pay.plugin.promocode.mapper;

import ru.ancap.framework.command.api.commands.operator.arguments.extractor.basic.PrimitiveExtractor;
import ru.ancap.pay.plugin.promocode.PromocodeAPI;

public class PromocodeExtractor extends PrimitiveExtractor<PromocodeAPI> {
    
    public PromocodeExtractor() {
        super(PromocodeAPI.class);
    }
    
    @Override
    protected PromocodeAPI provide(String name) {
        PromocodeAPI promocode = PromocodeAPI.find(name);
        if (promocode == null) throw new RuntimeException("Promocode not found");
        return promocode;
    }
    
}
