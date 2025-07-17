import request from "@/utils/request";
import {type GetCubeListForm} from "./pluginType";

export function getCubeList(){
    return request({
        url: '/cube/list',
        method: 'get'
    })
}

export function uploadPluginJar(file:File){
    const formData = new FormData();
    formData.append('files', file);
    return request({
        method:'post',
        url:'/jar/batch-upload',
        data:formData,
        timeout: 100000
    })
}

/**
 * 获取指定Cube的默认配置信息
 */
export function getCubeConfiguration(cubeId: string){
    return request({
        url: `/cube/${cubeId}/getConfiguration`,
        method: 'get',
    })
}

/**
 * 获取指定Cube的所有配置信息
 */
export function getAllCubeConfigurations(cubeId: string){
    return request({
        url: `/cube/${cubeId}/getAllConfigurations`,
        method: 'get',
    })
}

/**
 * 获取指定Cube的特定配置信息
 */
export function getCubeConfigurationById(cubeId: string, configId: string){
    return request({
        url: `/cube/${cubeId}/getConfiguration/${configId}`,
        method: 'get',
    })
}

/**
 * 更新指定Cube的默认配置信息
 */
export function updateCubeConfiguration(form: any){
    return request({
        url: '/cube/updateConfiguration',
        method: 'post',
        data: form
    })
}

/**
 * 更新指定Cube的特定配置信息
 */
export function updateCubeConfigurationMulti(form: any){
    return request({
        url: '/cube/updateConfiguration/multi',
        method: 'post',
        data: form
    })
}

/**
 * 删除指定Cube的特定配置
 */
export function removeCubeConfiguration(cubeId: string, configId: string){
    return request({
        url: `/cube/${cubeId}/removeConfiguration/${configId}`,
        method: 'delete',
    })
}

/**
 * 克隆指定Cube的配置
 */
export function cloneCubeConfiguration(form: any){
    return request({
        url: '/cube/cloneConfiguration',
        method: 'post',
        data: form
    })
}

// 修改现有的函数，增加错误处理
export function updateAngelCubeStatus(cubeId: string, action: 'START' | 'STOP') {
    return request({
        url: '/cube/angel/updateStatus',
        method: 'post',
        data: {
            cubeId: cubeId,
            action: action
        },
        timeout: 30000 // 增加超时时间，因为启动/停止可能需要时间
    })
}