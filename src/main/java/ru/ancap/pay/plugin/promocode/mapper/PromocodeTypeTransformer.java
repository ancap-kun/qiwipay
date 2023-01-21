package ru.ancap.pay.plugin.promocode.mapper;

import ru.ancap.framework.api.command.commands.transformer.basic.AbstractTransformer;
import ru.ancap.pay.plugin.promocode.PromocodeType;

public class PromocodeTypeTransformer extends AbstractTransformer<PromocodeType> {
    
    public PromocodeTypeTransformer() {
        super(PromocodeType.class);
    }
    
    @Override
    protected PromocodeType provide(String inserted) {
        return PromocodeType.valueOf(inserted.toUpperCase());
    }
}
