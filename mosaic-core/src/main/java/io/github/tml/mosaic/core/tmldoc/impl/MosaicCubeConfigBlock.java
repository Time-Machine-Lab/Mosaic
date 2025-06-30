package io.github.tml.mosaic.core.tmldoc.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.github.tml.mosaic.core.tmldoc.JsonDocBlock;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class MosaicCubeConfigBlock extends JsonDocBlock{

    private List<Class<? extends JsonDocBlock>> initBlocks = new ArrayList<>();

    private List<JsonDocBlock> blockList = new ArrayList<>();

    private static final String CONFIG = "config";

    @Override
    public boolean init(JSONObject json) {
        JSONArray configArray = json.getJSONArray(CONFIG);
        if(Objects.isNull(configArray)){
            return false;
        }
        for (Object object : configArray) {
            JSONObject jsonObject = (JSONObject) object;
            JsonDocBlock configBlock = new JsonDocBlock();
            for (Class<? extends JsonDocBlock> aClass : initBlocks) {
                try {
                    JsonDocBlock jsonDocBlock = aClass.newInstance();
                    if (jsonDocBlock.init(jsonObject)) {
                        configBlock.assembly(jsonDocBlock);

                    }
                }catch (Exception e){
                    log.error("init {} error:{}",aClass.getName(), e.getMessage());
                }
            }
            addItem(configBlock);
        }
        this.docProperties.put(CONFIG, blockList);
        return true;
    }

    public void addInitBlock(Class<? extends JsonDocBlock> blockClass){
        initBlocks.add(blockClass);
    }

    public JsonDocBlock getItem(int index){
        return blockList.get(index);
    }

    public void addItem(JsonDocBlock item){
        blockList.add(item);
    }
}
