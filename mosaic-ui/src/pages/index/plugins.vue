<script lang="ts" setup>
import {uploadPluginJar, getCubeList} from "@/api/plugin/pluginApi";
import {type Cube} from "@/api/plugin/pluginType";
import {useCubeStore} from '@/store/data/useCubeStore'
import MinecraftButtonComponent from "../../components/common/MinecraftButtonComponent.vue";
import MinecraftInputComponent from "../../components/common/MinecraftInputComponent.vue";
const cubeStore = useCubeStore()
const uploadRef = ref()
const upload = (item) => {
  uploadPluginJar(item.target.files[0]).then((res:any)=>{
    if(res.code == 200) {

    } else {
      //
    }
  })
}

const cubeList = computed(()=>{
  if(key.value==='')return cubeStore.cubeList
  return cubeStore.cubeList.filter((cube)=>{
    return cube.name.toLowerCase().includes(key.value.toLowerCase())
  })
})
const getCubeListFunction = () => {
  cubeStore.getCubes()
}
onMounted(()=>{
  getCubeListFunction()
})
const key = ref('')
</script>
<template>
  <div class="minecraft-header minecraft-glow">
    <h1>{{$t('menu.plugins')}}</h1>
  </div>
  <div class="operation">
    <MinecraftButtonComponent style="flex: 1;">
      <div class="mx-auto">
        <v-icon>mdi-upload</v-icon>
        <span>
          {{$t("plugins.upload")}}
        </span>
      </div>
    </MinecraftButtonComponent>
    <MinecraftInputComponent style="flex: 9" v-model="key" :placeholder="'Plugin Name'"></MinecraftInputComponent>
  </div>
  <ul class="cube-list">
    <li class="cube-list-item" v-for="cube in cubeList" :key="cube.cubeId">
      <CubeListItemComponent :cube="cube"></CubeListItemComponent>
    </li>
  </ul>
  <input type="file" accept=".jar" style="visibility: hidden" ref="uploadRef" @input="upload">

</template>
<style scoped lang="scss">
.cube-list {
  width: 100%;
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 24px;
  row-gap: 24px;
  .cube-list-item{
    //width: 40%;
    cursor: pointer;
  }
}
.upload-btn{
  position: relative;
  width: 90%;
  height: 48px;
  left: 50%;
  transform: translate(-50%);
}
.operation{
  width: 100%;
  display: flex;
  justify-content: space-between;
  padding: 16px 0;
  gap: 24px;
}
</style>
