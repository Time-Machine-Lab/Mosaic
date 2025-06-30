package io.github.tml.mosaic.core.tmldoc.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;


public class Demo {

    public static void main(String[] args) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll( Map.of("name", "welsir", "type", "string", "desc", "welsir", "required", true, "defaultValue", "https://api.openai.com/v1/chat/completions"));

        jsonArray.add(jsonObject);
        JSONObject init = new JSONObject();
        init.put("config", jsonArray);

        MosaicCubeConfigBlock mosaicCubeConfig = new MosaicCubeConfigBlock();
        mosaicCubeConfig.addInitBlock(NameBlock.class);
        mosaicCubeConfig.addInitBlock(ValueBlock.class);
        mosaicCubeConfig.init(init);

        System.out.println(
                mosaicCubeConfig.getItem(0)
                .getDoc(ValueBlock.class)
                .getDefaultValue()
        );

        System.out.println(mosaicCubeConfig.getItem(0).getProperties().getAll());
    }
}
