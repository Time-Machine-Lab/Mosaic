package io.github.tml.mosaic.core.tmldoc;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDocBlock<T extends DocProperties> implements DocBlock<T> {

    protected T docProperties;

    protected final Map<Class<? extends DocBlock>, DocBlock<T>> docBlockMap = new HashMap<>();

    public AbstractDocBlock(T docProperties) {
        this.docProperties = docProperties;
    }

    public <B extends DocBlock> B getDoc(Class<B> clazz) {
        return (B) docBlockMap.get(clazz);
    }

    @Override
    public void assembly(DocBlock<T> block) {
        docBlockMap.put(block.getClass(), block);
        docProperties.putAll(block.getProperties());
    }

    @Override
    public T getProperties() {
        return docProperties;
    }
}
