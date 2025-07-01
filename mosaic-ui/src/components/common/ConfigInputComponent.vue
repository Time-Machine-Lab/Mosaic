<script lang="ts" setup>
import {defineProps,defineEmits} from "vue";
const emit = defineEmits(['update:modelValue']);

const props = defineProps({
  label:{
    default: ''
  },
  type:{
    default:'string'
  },
  modelValue: {
  }
})
const valueProxy = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});

</script>
<template>
  <v-label>{{props.label}}</v-label>
  <v-text-field variant="outlined" v-model="valueProxy" v-if="props.type === 'string'"></v-text-field>
  <v-number-input type="number" v-model="valueProxy" controlVariant="default" variant="outlined" v-else-if="props.type==='number'"></v-number-input>
  <v-switch :true-value="true" :false-value="false" inset v-model="valueProxy" v-else-if="props.type === 'boolean'" ></v-switch>
</template>
<style scoped lang="scss">

</style>
