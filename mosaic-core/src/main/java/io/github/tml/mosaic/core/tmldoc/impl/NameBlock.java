package io.github.tml.mosaic.core.tmldoc.impl;

import com.alibaba.fastjson.JSONObject;
import io.github.tml.mosaic.core.tmldoc.JsonDocBlock;

import java.util.stream.Stream;

public class NameBlock extends JsonDocBlock {

    private static final String NAME = "name";

    private static final String DESCRIPTION = "desc";

    @Override
    public boolean init(JSONObject json) {
        if (!Stream.of(NAME, DESCRIPTION).allMatch(json::containsKey)) {
            return false;
        }
        this.docProperties.put(NAME, json.get(NAME));
        this.docProperties.put(DESCRIPTION, json.get(DESCRIPTION));
        return true;
    }

    public String getName(){
        return this.docProperties.get(NAME, String.class);
    }

    public String getDescription(){
        return this.docProperties.get(DESCRIPTION, String.class);
    }

}
