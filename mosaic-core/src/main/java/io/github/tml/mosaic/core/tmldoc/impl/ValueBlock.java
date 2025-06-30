package io.github.tml.mosaic.core.tmldoc.impl;

import com.alibaba.fastjson.JSONObject;
import io.github.tml.mosaic.core.tmldoc.JsonDocBlock;

import java.util.stream.Stream;

public class ValueBlock extends JsonDocBlock {

    private static final String DEFAULT_VALUE = "defaultValue";

    private static final String REQUIRED = "required";

    private static final String TYPE = "type";

    @Override
    public boolean init(JSONObject json) {
        if (!Stream.of(DEFAULT_VALUE, REQUIRED, TYPE).allMatch(json::containsKey)) {
             return false;
        }
        this.docProperties.put(DEFAULT_VALUE, json.get(DEFAULT_VALUE));
        this.docProperties.put(REQUIRED, json.get(REQUIRED));
        this.docProperties.put(TYPE, json.get(TYPE));
        return true;
    }

    public boolean getRequired(){
        return this.docProperties.get(REQUIRED, Boolean.class);
    }

    public String getDefaultValue(){
        return this.docProperties.get(DEFAULT_VALUE, String.class);
    }

    public Class<?> getType(){
        String className = this.docProperties.get(TYPE, String.class);
        return null;
    }

}
