package io.github.tml.mosaic.core.tmldoc;

import com.alibaba.fastjson.JSONObject;

public class JsonDocBlock extends AbstractDocBlock<JsonProperties>{

    public JsonDocBlock() {
        super(new JsonProperties());
    }

    public boolean init(JSONObject json){
        return true;
    }

}
