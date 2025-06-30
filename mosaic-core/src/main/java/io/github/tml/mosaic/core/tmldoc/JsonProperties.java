package io.github.tml.mosaic.core.tmldoc;

import com.alibaba.fastjson.JSONObject;

public class JsonProperties extends DocProperties<JSONObject> {

    public JsonProperties() {
        super(new JSONObject());
    }

    @Override
    public Object get(String key) {
        return properties.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return properties.getObject(key, clazz);
    }

    @Override
    public void put(String key, Object value) {
        properties.put(key, value);
    }

    @Override
    public void putAll(DocProperties<JSONObject> properties) {
        this.properties.putAll(properties.getAll());
    }
}
