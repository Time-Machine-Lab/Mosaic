package io.github.tml.mosaic.core.tmldoc;

public abstract class DocProperties<T> {

    protected T properties;

    public DocProperties(T properties) {
        this.properties = properties;
    }

    public T getAll(){
        return properties;
    }

    public abstract Object get(String key);

    public abstract <T> T get(String key, Class<T> clazz);

    public abstract void put(String key, Object value);

    public abstract void putAll(DocProperties<T> properties);
}
