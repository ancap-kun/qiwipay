package ru.ancap.pay.plugin.promocode.mapper;

import ru.ancap.framework.command.api.commands.operator.arguments.extractor.basic.PrimitiveExtractor;
import ru.ancap.pay.plugin.promocode.PromocodeType;

public class PromocodeTypeTransformer extends PrimitiveExtractor<PromocodeType> {
    
    public PromocodeTypeTransformer() {
        super(PromocodeType.class);
    }
    
    @Override
    protected PromocodeType provide(String inserted) {
        return PromocodeType.valueOf(inserted.toUpperCase());
    }
}
